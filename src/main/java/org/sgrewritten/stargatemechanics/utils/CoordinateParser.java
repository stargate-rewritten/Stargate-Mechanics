package org.sgrewritten.stargatemechanics.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateParser {
    private static Pattern RADIUS_GREATER_THAN = Pattern.compile(
            "(r>)(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)|(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)(<r)");
    private static Pattern RADIUS_LESSER_THAN = Pattern.compile(
            "(r<)(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)|(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)(>r)");
    private static Pattern RADIUS_EQUALS = Pattern.compile(
            "(r=)(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)|(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)(=r)");
    private static Pattern INVALID_PATTERN = Pattern.compile("(>r<)|(<r>)|[^r<>=.;e0-9]");

    public static Location getLocationFromPortal(RealPortal origin) throws ParseException {
        JsonElement element = origin.getMetadata(MetaData.DESTINATION_COORDS.name());
        if (element == null) {
            return null;
        }
        JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
        if (origin.hasFlag(MechanicsFlag.COORD.getCharacterRepresentation())) {
            return getLocationFromExpression(jsonPrimitive.getAsString(), origin);
        }
        if (origin.hasFlag(MechanicsFlag.RANDOM_COORD.getCharacterRepresentation())) {
            return getRandomLocationFromExpression(jsonPrimitive.getAsString(), origin);
        }
        return null;
    }

    public static Location getLocationFromExpression(@NotNull String expression, @NotNull RealPortal origin) throws ParseException {
        BlockVector xyz;
        Location topLeft = origin.getGate().getTopLeft();
        String[] worldSeparated = expression.split(";");
        String coordExpression = worldSeparated[0];
        if (coordExpression.startsWith("^")) {
            int value = NumberParser.parseInt(coordExpression.replace("\\^", ""));
            xyz = new BlockVector(topLeft.getBlockX(), topLeft.getBlockY(), topLeft.getBlockZ());
            xyz.add(origin.getGate().getFacing().getDirection().multiply(value));
        } else {
            switch ((int) expression.chars().filter((aChar) -> aChar == ',').count()) {
                case 1 -> {
                    String[] expressionSplit = coordExpression.split(",");
                    int x = parseCoord(expressionSplit[0], topLeft.getBlockX());
                    int z = parseCoord(expressionSplit[1], topLeft.getBlockZ());
                    int y = topLeft.getWorld().getHighestBlockYAt(x, z);
                    xyz = new BlockVector(x, y, z);
                }
                case 2 -> {
                    String[] expressionSplit = coordExpression.split(",");
                    int x = parseCoord(expressionSplit[0], topLeft.getBlockX());
                    int y = parseCoord(expressionSplit[1], topLeft.getBlockY());
                    int z = parseCoord(expressionSplit[2], topLeft.getBlockZ());
                    xyz = new BlockVector(x, y, z);
                }
                default -> {
                    int x = parseCoord(coordExpression, topLeft.getBlockX(), 'X');
                    int z = parseCoord(coordExpression, topLeft.getBlockZ(), 'Z');
                    int y;
                    try {
                        y = parseCoord(coordExpression, topLeft.getBlockY(), 'Y');
                    } catch (IllegalArgumentException | ParseException e) {
                        y = topLeft.getWorld().getHighestBlockYAt(x, z);
                    }
                    xyz = new BlockVector(x, y, z);
                }
            }
        }
        World targetWorld = parseWorld(worldSeparated, origin.getGate().getTopLeft().getWorld());
        return new Location(targetWorld, xyz.getX(), xyz.getY(), xyz.getZ());
    }

    private static int parseCoord(String expression, int originCoord, char axis) throws ParseException {
        String regex = axis + "=(.*?)([XYZ]|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            return parseCoord(matcher.group(1), originCoord);
        } else {
            throw new ParseException("Could not find match from pattern " + regex);
        }
    }

    private static int parseCoord(String coordString, int originCoord) throws ParseException {
        if (coordString.charAt(0) == '~') {
            return originCoord + NumberParser.parseInt(coordString.replaceAll("^~", ""));
        } else {
            return NumberParser.parseInt(coordString);
        }
    }


    public static Location getRandomLocationFromExpression(String expression, RealPortal origin) throws ParseException {
        if (INVALID_PATTERN.matcher(expression.toLowerCase()).find()) {
            throw new ParseException("Invalid expression");
        }
        String[] splitExpression = expression.toLowerCase().split(";");
        String radiusExpression = splitExpression[0];
        World world = parseWorld(splitExpression, origin.getGate().getTopLeft().getWorld());
        if (radiusExpression.isBlank()) {
            return getCompletelyRandomDestination(world);
        }
        int radiusLowerBound = 0;
        int radiusUpperBound = Integer.MAX_VALUE;
        int radius = -1;
        Matcher equals = RADIUS_EQUALS.matcher(radiusExpression);
        if (equals.find()) {
            radius = getValueFromMatch(equals);
        } else {
            Matcher greaterThan = RADIUS_GREATER_THAN.matcher(radiusExpression);
            Matcher lesserThan = RADIUS_LESSER_THAN.matcher(radiusExpression);
            if (greaterThan.find()) {
                radiusLowerBound = getValueFromMatch(greaterThan);
            }
            if (lesserThan.find()) {
                radiusUpperBound = getValueFromMatch(lesserThan);
            }
            try {
                radius = new Random().nextInt(radiusLowerBound, radiusUpperBound);
            } catch (IllegalArgumentException e) {
                throw new ParseException(e.getMessage());
            }
        }
        if (radius == -1) {
            radius = NumberParser.parseInt(radiusExpression);
        }
        BlockVector vector = new BlockVector(radius, 0, 0);
        vector.rotateAroundY(new Random().nextDouble(0, 2 * Math.PI));

        Location topLeft = origin.getGate().getTopLeft();
        Location location = new Location(world, topLeft.getBlockX(), topLeft.getBlockY(), topLeft.getBlockZ());
        location.add(vector);
        return location.toBlockLocation();
    }

    private static Location getCompletelyRandomDestination(World world) {
        Random random = new Random();
        WorldBorder worldBorder = world.getWorldBorder();
        int centerX = worldBorder.getCenter().getBlockX();
        int centerZ = worldBorder.getCenter().getBlockZ();
        int x = random.nextInt((int) (centerX - worldBorder.getSize() / 2), (int) (centerX + worldBorder.getSize() / 2));
        int z = random.nextInt((int) (centerZ - worldBorder.getSize() / 2), (int) (centerZ + worldBorder.getSize() / 2));
        return world.getHighestBlockAt(x, z).getLocation();
    }

    private static World parseWorld(String[] args, World originWorld) throws ParseException {
        if (args.length == 2) {
            try {
                return Bukkit.getWorlds().get(NumberParser.parseInt(args[1]));
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException(e.getMessage());
            }
        } else if (args.length > 2) {
            throw new ParseException("Too many ';'");
        } else {
            return originWorld;
        }
    }

    private static int getValueFromMatch(Matcher matcher) throws ParseException {
        try {
            return NumberParser.parseInt(matcher.group(2));
        } catch (NullPointerException e) {
            return NumberParser.parseInt(matcher.group(4));
        }
    }
}

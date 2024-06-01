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

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateParser {

    private CoordinateParser() {
        throw new IllegalStateException("Utility class");
    }

    private static final String NUMBER = "([+-]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?";
    private static final Pattern RADIUS_GREATER_THAN = Pattern.compile("r>(" + NUMBER + ")|(" + NUMBER + ")<r", Pattern.CASE_INSENSITIVE);
    private static final Pattern RADIUS_LESSER_THAN = Pattern.compile("r<(" + NUMBER + ")|(" + NUMBER + ")>r", Pattern.CASE_INSENSITIVE);
    private static final Pattern RADIUS_EQUALS = Pattern.compile("r=(" + NUMBER + ")|(" + NUMBER + ")=r", Pattern.CASE_INSENSITIVE);
    private static final Pattern INVALID_RANDOM_PATTERN = Pattern.compile("(>r<)|(<r>)|[^r<>=.e0-9]", Pattern.CASE_INSENSITIVE);
    private static final Pattern INVALID_DEST_PATTERN = Pattern.compile("[^=.,~e0-9xyz\\-]", Pattern.CASE_INSENSITIVE);
    private static final Pattern X_Z_PATTERN = Pattern.compile("(" + NUMBER + "),(" + NUMBER + ")", Pattern.CASE_INSENSITIVE);
    private static final Pattern X_Y_Z_PATTERN = Pattern.compile("(" + NUMBER + "),(" + NUMBER + "),(" + NUMBER + ")", Pattern.CASE_INSENSITIVE);
    private static final Pattern X_PATTERN = Pattern.compile("x=(" + NUMBER + ")|(" + NUMBER + ")=x", Pattern.CASE_INSENSITIVE);
    private static final Pattern Y_PATTERN = Pattern.compile("y=(" + NUMBER + ")|(" + NUMBER + ")=y", Pattern.CASE_INSENSITIVE);
    private static final Pattern Z_PATTERN = Pattern.compile("z=(" + NUMBER + ")|(" + NUMBER + ")=z", Pattern.CASE_INSENSITIVE);
    private static final Pattern RELATIVE_X_Z_PATTERN = Pattern.compile("~(" + NUMBER + "),~(" + NUMBER + ")", Pattern.CASE_INSENSITIVE);
    private static final Pattern RELATIVE_X_Y_Z_PATTERN = Pattern.compile("~(" + NUMBER + "),~(" + NUMBER + "),~(" + NUMBER + ")", Pattern.CASE_INSENSITIVE);

    private static final Random RANDOM = new Random();

    public static Location getLocationFromPortal(RealPortal origin) throws ParseException {
        JsonElement element = origin.getMetadata(MetaData.DESTINATION_COORDS.name());
        if (element == null) {
            return null;
        }
        JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
        if (origin.hasFlag(MechanicsFlag.GENERATE)) {
            return getLocationFromExpression(jsonPrimitive.getAsString(), origin);
        }
        return null;
    }

    public static Location getLocationFromExpression(@NotNull String expression, @NotNull RealPortal origin) throws ParseException {
        Map<CoordinateDescriptorType, String> expressionSplit = getExpressions(expression);
        World world;
        if (expressionSplit.containsKey(CoordinateDescriptorType.WORLD)) {
            world = parseWorld(expressionSplit.get(CoordinateDescriptorType.WORLD));
        } else {
            world = origin.getExit().getWorld();
        }

        if (!expressionSplit.containsKey(CoordinateDescriptorType.NON_RANDOM) && !expressionSplit.containsKey(CoordinateDescriptorType.RANDOM)) {
            return getCompletelyRandomDestination(world);
        }

        BlockVector randomDelta;
        if (expressionSplit.containsKey(CoordinateDescriptorType.RANDOM)) {
            randomDelta = parseRandom(expressionSplit.get(CoordinateDescriptorType.RANDOM));
        } else {
            randomDelta = new BlockVector();
        }

        Location destinationCenter;
        AtomicBoolean hasDefinedY = new AtomicBoolean();
        if (expressionSplit.containsKey(CoordinateDescriptorType.NON_RANDOM)) {
            destinationCenter = parseDestination(expressionSplit.get(CoordinateDescriptorType.NON_RANDOM), origin, world, hasDefinedY);
        } else {
            destinationCenter = origin.getGate().getTopLeft();
        }
        destinationCenter.add(randomDelta);
        if (!hasDefinedY.get()) {
            destinationCenter = world.getHighestBlockAt(destinationCenter.getBlockX(), destinationCenter.getBlockZ()).getLocation();
        }
        return destinationCenter;
    }

    private static Location parseDestination(String expression, RealPortal origin, World world, AtomicBoolean hasDefinedY) throws ParseException {
        Matcher xZ = X_Z_PATTERN.matcher(expression);
        if (xZ.matches()) {
            double x = Double.parseDouble(xZ.group(1));
            double z = Double.parseDouble(xZ.group(3));
            return new Location(world, x, 0, z);
        }
        Matcher xYZ = X_Y_Z_PATTERN.matcher(expression);
        if (xYZ.matches()) {
            hasDefinedY.set(true);
            double x = Double.parseDouble(xYZ.group(1));
            double y = Double.parseDouble(xYZ.group(3));
            double z = Double.parseDouble(xYZ.group(5));
            return new Location(world, x, y, z);
        }
        Matcher relativeXZ = RELATIVE_X_Z_PATTERN.matcher(expression);
        if (relativeXZ.matches()) {
            hasDefinedY.set(true);
            Location topLeft = origin.getGate().getTopLeft();
            double x = topLeft.getX() + Double.parseDouble(relativeXZ.group(1));
            double y = topLeft.getY();
            double z = topLeft.getZ() + Double.parseDouble(relativeXZ.group(3));
            return new Location(world, x, y, z);
        }
        Matcher relativeXYZ = RELATIVE_X_Y_Z_PATTERN.matcher(expression);
        if (relativeXYZ.matches()) {
            hasDefinedY.set(true);
            Location topLeft = origin.getGate().getTopLeft();
            double x = topLeft.getX() + Double.parseDouble(relativeXYZ.group(1));
            double y = topLeft.getY() + Double.parseDouble(relativeXYZ.group(3));
            double z = topLeft.getZ() + Double.parseDouble(relativeXYZ.group(5));
            return new Location(world, x, y, z);
        }
        Matcher xMatcher = X_PATTERN.matcher(expression);
        if (!xMatcher.find()) {
            throw new ParseException("Lacking x coordinate information");
        }
        double x = getValueFromMatch(xMatcher, 1, 3);
        Matcher yMatcher = Y_PATTERN.matcher(expression);
        double y;
        if (yMatcher.find()) {
            hasDefinedY.set(true);
            y = getValueFromMatch(yMatcher, 1, 3);
        } else {
            y = 0;
        }

        Matcher zMatcher = Z_PATTERN.matcher(expression);
        if (!zMatcher.find()) {
            throw new ParseException("Lacking z coordinate information");
        }
        double z = getValueFromMatch(zMatcher, 1, 3);
        return new Location(world, x, y, z);
    }

    private static BlockVector parseRandom(String expression) throws ParseException {
        double radius;
        Matcher radiusEquals = RADIUS_EQUALS.matcher(expression);
        if (radiusEquals.matches()) {
            radius = getValueFromMatch(radiusEquals, 1, 3);
        } else {
            Matcher radiusLesserThan = RADIUS_LESSER_THAN.matcher(expression);
            double upperBound;
            if (radiusLesserThan.find()) {
                upperBound = getValueFromMatch(radiusLesserThan, 1, 3);
            } else {
                upperBound = Integer.MAX_VALUE;
            }

            Matcher radiusGreaterThan = RADIUS_GREATER_THAN.matcher(expression);
            double lowerBound;
            if (radiusGreaterThan.find()) {
                lowerBound = getValueFromMatch(radiusGreaterThan, 1, 3);
            } else {
                lowerBound = 0;
            }
            if (lowerBound >= upperBound) {
                throw new ParseException("Upperbound lesser or equal to lowerbound");
            }
            double randomDouble = RANDOM.nextDouble(lowerBound == 0 ? 0 : Math.sqrt(lowerBound), Math.sqrt(upperBound));
            radius = randomDouble * randomDouble;
        }
        BlockVector vector = new BlockVector(radius, 0, 0);
        vector.rotateAroundY(RANDOM.nextDouble(0, 2 * Math.PI));
        return vector.toBlockVector();
    }

    private static World parseWorld(String expression) {
        return Bukkit.getWorld(expression);
    }

    private static Location getCompletelyRandomDestination(World world) {
        WorldBorder worldBorder = world.getWorldBorder();
        int centerX = worldBorder.getCenter().getBlockX();
        int centerZ = worldBorder.getCenter().getBlockZ();
        int x = RANDOM.nextInt((int) (centerX - worldBorder.getSize() / 2), (int) (centerX + worldBorder.getSize() / 2));
        int z = RANDOM.nextInt((int) (centerZ - worldBorder.getSize() / 2), (int) (centerZ + worldBorder.getSize() / 2));
        return world.getHighestBlockAt(x, z).getLocation();
    }

    private static double getValueFromMatch(Matcher matcher, int... indices) throws ParseException {
        for (int index : indices) {
            String matchString = matcher.group(index);
            if (matchString != null) {
                return Double.parseDouble(matchString);
            }
        }
        throw new ParseException("No match");
    }

    public static boolean isRandomCoordinateDestination(RealPortal realPortal) {
        JsonElement metaDataElement = realPortal.getMetadata(MetaData.DESTINATION_COORDS.name());
        if (metaDataElement == null || metaDataElement.getAsString().isBlank()) {
            return true;
        }
        String destinationString = metaDataElement.getAsString();
        Map<CoordinateDescriptorType, String> expressions = null;
        try {
            expressions = getExpressions(destinationString);
        } catch (ParseException e) {
            return false;
        }
        return expressions.containsKey(CoordinateDescriptorType.RANDOM);
    }

    private static Map<CoordinateDescriptorType, String> getExpressions(String expression) throws ParseException {
        String[] expressions = expression.replace(" ", "").split(";");
        if (expressions.length == 1 && expressions[0].isBlank()) {
            return Map.of();
        }
        Map<CoordinateDescriptorType, String> output = new EnumMap<>(CoordinateDescriptorType.class);
        for (String subExpression : expressions) {
            CoordinateDescriptorType coordinateDescriptorType = determineExpressionType(subExpression);
            if (output.containsKey(coordinateDescriptorType)) {
                throw new ParseException("Sub-expressions of duplicate type");
            }
            output.put(coordinateDescriptorType, subExpression);
        }
        return output;
    }

    private static CoordinateDescriptorType determineExpressionType(String expression) throws ParseException {
        World world = Bukkit.getWorld(expression);
        if (world != null) {
            return CoordinateDescriptorType.WORLD;
        }
        if (!INVALID_RANDOM_PATTERN.matcher(expression).find()) {
            if (RADIUS_EQUALS.matcher(expression).matches() || RADIUS_GREATER_THAN.matcher(expression).find() || RADIUS_LESSER_THAN.matcher(expression).find()) {
                return CoordinateDescriptorType.RANDOM;
            }
        }
        if(INVALID_DEST_PATTERN.matcher(expression).find()){
            throw new ParseException("Illegal chars, or non existing world");
        }
        if (X_Z_PATTERN.matcher(expression).matches() || X_Y_Z_PATTERN.matcher(expression).matches()) {
            return CoordinateDescriptorType.NON_RANDOM;
        }
        if (RELATIVE_X_Z_PATTERN.matcher(expression).matches() || RELATIVE_X_Y_Z_PATTERN.matcher(expression).matches()) {
            return CoordinateDescriptorType.NON_RANDOM;
        }
        Matcher x_pattern_matcher = X_PATTERN.matcher(expression);
        Matcher y_pattern_matcher = Y_PATTERN.matcher(expression);
        y_pattern_matcher.find(); // Optional argument / does not need to be considered
        Matcher z_pattern_matcher = Z_PATTERN.matcher(expression);
        if (x_pattern_matcher.find() && !x_pattern_matcher.find() && z_pattern_matcher.find() && !z_pattern_matcher.find()
                && !y_pattern_matcher.find()) {
            return CoordinateDescriptorType.NON_RANDOM;
        }
        throw new ParseException("Invalid pattern");
    }

    enum CoordinateDescriptorType {
        RANDOM, NON_RANDOM, WORLD;
    }
}

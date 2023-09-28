package org.sgrewritten.stargatemechanics.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateParser {

    public static Location getLocationFromExpression(String expression, RealPortal origin) throws ParseException {
        BlockVector xyz;
        Location topLeft = origin.getGate().getTopLeft();
        String[] worldSeparated = expression.split(";");
        if(worldSeparated.length > 2){
            throw new ParseException("Invalid input");
        }
        String coordExpression = worldSeparated[0];
        if(coordExpression.startsWith("^")){
            int value = NumberParser.parseInt(coordExpression.replace("\\^", ""));
            xyz = new BlockVector(topLeft.getBlockX(), topLeft.getBlockY(), topLeft.getBlockZ());
            xyz.add(origin.getGate().getFacing().getDirection().multiply(value));
        } else {
            switch ((int) expression.chars().filter((aChar) -> aChar == ',').count()) {
                case 1 -> {
                    String[] expressionSplit = coordExpression.split(",");
                    int x = parseCoord(expressionSplit[0], topLeft.getBlockX());
                    int z = parseCoord(expressionSplit[1], topLeft.getBlockZ());
                    int y = topLeft.getWorld().getHighestBlockYAt(x,z);
                    xyz = new BlockVector(x,y,z);
                }
                case 2 -> {
                    String[] expressionSplit = coordExpression.split(",");
                    int x = parseCoord(expressionSplit[0], topLeft.getBlockX());
                    int y = parseCoord(expressionSplit[1], topLeft.getBlockY());
                    int z = parseCoord(expressionSplit[2], topLeft.getBlockZ());
                    xyz = new BlockVector(x,y,z);
                }
                default -> {
                    int x = parseCoord(coordExpression, topLeft.getBlockX(), 'X');
                    int z = parseCoord(coordExpression, topLeft.getBlockZ(), 'Z');
                    int y;
                    try {
                        y = parseCoord(coordExpression, topLeft.getBlockY(), 'Y');
                    } catch (IllegalArgumentException | ParseException e) {
                        y = topLeft.getWorld().getHighestBlockYAt(x,z);
                    }
                    xyz = new BlockVector(x,y,z);
                }
            }
        }
        World targetWorld;
        if(worldSeparated.length == 1){
            targetWorld = topLeft.getWorld();
        } else {
            targetWorld = Bukkit.getWorlds().get(Integer.parseInt(worldSeparated[1]));
        }
        return new Location(targetWorld, xyz.getX(), xyz.getY(), xyz.getZ());
    }

    private static int parseCoord(String expression, int originCoord, char axis) throws ParseException {
        String regex = axis +"=(.*?)([XYZ]|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);
        if(matcher.find()) {
            return parseCoord(matcher.group(1), originCoord);
        } else {
            throw new ParseException("Could not find match from pattern " + regex);
        }
    }

    private static int parseCoord(String coordString, int originCoord) throws ParseException {
        if(coordString.charAt(0) == '~'){
            return originCoord + NumberParser.parseInt(coordString.replaceAll("^~",""));
        } else {
            return NumberParser.parseInt(coordString);
        }
    }



    public static Location getRandomLocationFromExpression(String expression) throws ParseException{
        return null;
    }
}

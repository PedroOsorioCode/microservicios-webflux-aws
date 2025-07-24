package co.com.microservice.aws.application.helpers.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class FileStructureValidator {
    private static final Pattern patternLines = Pattern.compile("\\r?\\n");

    public static String[] getFileLines(String data) {
        return patternLines.split(data);
    }

    public static String getDataLine(String[] fileLines, int num) {
        return fileLines[num - 1];
    }

    private static boolean validatByRegEx(String regex, String data) {
        return Pattern.matches(regex, data);
    }

    public static String changeZipToTxt(String zipPath) {
        var toReplace = ".zip";
        var replacement = ".txt";
        int lastIndex = zipPath.lastIndexOf(toReplace);
        if (lastIndex == -1) {
            return zipPath + replacement;
        }
        return zipPath.substring(0, lastIndex) +
                replacement +
                zipPath.substring(lastIndex + toReplace.length());
    }
}
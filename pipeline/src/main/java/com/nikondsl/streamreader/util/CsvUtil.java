package com.nikondsl.streamreader.util;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class CsvUtil {
    /**
     * Split a CSV String like "1,2,3" into String array.
     * Can work with quote enclosed strings like "aa bb","zz,,,z"
     * which are interpreted close to Excel behaviour.
     *
     * @param line String
     * @return String[]
     */
    public static String[] csvLineParser(String line) {
        return csvLineParser(line, true);
    }

    public static String[] csvLineParser(String line, boolean classicCscParser) {
        return csvLineParser(line, delimiters, classicCscParser);
    }

    private static final Set<Character> delimiters = Collections.unmodifiableSet(Sets.newHashSet(',', ';', '\t'));
//    public static Set<Character> getDelimiterCharacter(Delimiter delimiter){
//        switch (delimiter){
//            case COMMA:
//                return Collections.unmodifiableSet(Sets.newHashSet(','));
//            case SEMICOLON:
//                return Collections.unmodifiableSet(Sets.newHashSet(';'));
//            case TAB:
//                return Collections.unmodifiableSet(Sets.newHashSet('\t'));
//            case SPACE:
//                return Collections.unmodifiableSet(Sets.newHashSet(' '));
//            case PIPE:
//                return Collections.unmodifiableSet(Sets.newHashSet('|'));
//            case OTHER:
//            default:
//                throw new IllegalArgumentException("Invalid type: "+delimiter);
//        }
//    }

    public static String[] csvLineParser(String line, Character... delimiters) {
        return csvLineParser(line, true, delimiters);
    }

    public static String[] csvLineParser(String line, boolean classicCscParser, Character... delimiters) {
        final List<String> fields = csvLineParseList(line, classicCscParser, delimiters);
        final String[] fieldsArr = new String[fields.size()];
        fields.toArray(fieldsArr);
        return fieldsArr;
    }

    public static String[] csvLineParser(final String line, final Set<Character> delimitersSet, boolean classicCscParser) {
        final List<String> fields = csvLineParseList(line, delimitersSet, classicCscParser);
        final String[] fieldsArr = new String[fields.size()];
        fields.toArray(fieldsArr);
        return fieldsArr;
    }

    public static List<String> csvLineParseList(String line, final Character... delimiters) {
        return csvLineParseList(line, new HashSet<>(Arrays.asList(delimiters)), true);
    }

    public static List<String> csvLineParseList(String line, boolean classicCscParser, final Character... delimiters) {
        return csvLineParseList(line, new HashSet<>(Arrays.asList(delimiters)), classicCscParser);
    }

    public static List<String> csvLineParseList(String line, final Set<Character> delimitersSet) {
        return csvLineParseList(line, delimitersSet, true);
    }

    public static List<String> csvLineParseList(String line, final Set<Character> delimitersSet, boolean classicCscParser) {
        if (Strings.isNullOrEmpty(line)) {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(line, "\n\r");
        StringBuilder newline = new StringBuilder(line.length());
        while (st.hasMoreTokens()) {
            newline.append(st.nextToken());
        }
        line = newline.toString();
        List<String> fields = new ArrayList<>();
        char[] chars = line.toCharArray();
        StringBuilder field = new StringBuilder(1024);
        boolean isInsideQuotes = false;
        boolean needSkip = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            boolean isSeparator = needSkip || delimitersSet.contains(c);
            if (isInsideQuotes) {
                isSeparator = false;
            }
            char nextChar = '\u0000';
            if (i + 1 < chars.length) {
                nextChar = chars[i + 1];
            }
            if (!classicCscParser && isSeparator && nextChar==' ') {
                needSkip=true;
                continue;
            }
            needSkip=false;
            if (isSeparator) {
                fields.add(field.toString());
                field = new StringBuilder(1024);
                continue;
            }
            if (isInsideQuotes) {
                boolean isNextCharSeparator=true;
                if (i + 1 < chars.length) {
                    isNextCharSeparator = delimitersSet.contains(nextChar);
                }
                if (isNextCharSeparator && c == '"') {
                    if (i < chars.length - 1 && chars[i + 1] == '"') { // double quote inside quotes
                        field.append('"');
                        i++; // skip next quota
                        continue;
                    }
                    isInsideQuotes = false; // end of quoted block
                    continue;
                }
                else {
                    if (c == '"' && nextChar == '"') {
                        field.append(c);
                        i++;
                        continue;
                    }
                    field.append(c);
                    continue;
                }
            }
            // not inside quotes:
            if (c == '"' && field.length() == 0) {
                isInsideQuotes = true;
                continue;
            }
            field.append(c);
        }
        fields.add(field.toString());
        return fields;
    }

    public static String arrayToCSV(String[] fields) {
        return arrayToCSV(fields, ";");
    }

    public static String arrayToCSV(Collection<String> fields) {
        return arrayToCSV(fields.toArray(new String[fields.size()]), ";");
    }

    public static String arrayToCSV(Collection<String> fields, String separator) {
        return arrayToCSV(fields.toArray(new String[fields.size()]), separator);
    }

    public static String arrayToCSV(Collection fields, String delimiter, String method) {
        return arrayToCSV(fields.toArray(), delimiter, method);
    }

    public static String arrayToCSV(Object[] fields, String delimiter, String method) {
        return arrayToCSV(fields, delimiter, method, 0);
    }

    public static String arrayToCSV(Object[] fields, String delimiter, String method, int length) {
        if (Strings.isNullOrEmpty(method)){
            method = "toString";
        }
        StringBuilder ret = new StringBuilder(1024);
        int i = 0;
        if (fields == null) return "";
        Class<?> clazzo = null;
        Method m = null;
        while (i < fields.length) {
            Object fieldo = fields[i];
            if (length > 0 && i > length) break;
            i++;
            if (fieldo == null) {
                ret.append("null").append(delimiter);
                continue;
            }
            final Class<? extends Object> clazz = fieldo.getClass();
            if (clazz != clazzo) {
                clazzo = clazz;
                try {
                    m = clazz.getMethod(method, (java.lang.Class[]) null);
                    m.setAccessible(true);
                }
                catch (NoSuchMethodException ex) {
                    throw new RuntimeException("Cannot find method " + method + " in class " + clazz.getCanonicalName(), ex);
                }
            }
            Object field = new Object();
            try {
                field = m.invoke(fieldo, (java.lang.Object[]) null);
            }
            catch (Exception ex) {
                throw new RuntimeException("Cannot invoke method " + m + " in class " + clazz.getCanonicalName(), ex);
            }
            if (field == null) {
                return "";
            }
            String fieldstr = field.toString();
            if (fieldstr.contains("\"")) {
                fieldstr = doubleQuotas(fieldstr);
            }
            if (containsDefaultCsvSeparators(fieldstr)) {
                if (fieldstr.startsWith("\"") && fieldstr.endsWith("\"")) {
                    ret.append(fieldstr).append(delimiter);
                    continue;
                }
                ret.append("\"").append(fieldstr).append("\"").append(delimiter);
                continue;
            }
            ret.append(fieldstr).append(delimiter);
        }
        if (ret.length() - delimiter.length() > 0) { ret.setLength(ret.length() - delimiter.length()); } else { ret.setLength(0); }
        return ret.toString();
    }

    public static String arrayToCSV(String[] fields, String delimiter) {
        if (fields == null || fields.length==0) return "";
        StringBuilder ret = new StringBuilder(1024);
        int i = 0;
        while (i < fields.length) {
            String field = fields[i];
            i++;
            if (field == null || field.length() == 0) {
                ret.append(delimiter);
                continue;
            }
            if (field.contains("\"")) {
                field = doubleQuotas(field);
            }
            if (containsDefaultCsvSeparators(field)) {
                if (field.startsWith("\"") && field.endsWith("\"")) {
                    ret.append(field).append(delimiter);
                    continue;
                }
                ret.append("\"").append(field).append("\"").append(delimiter);
                continue;
            }
            ret.append(field).append(delimiter);
        }
        if (ret.length() - delimiter.length() > 0) { ret.setLength(ret.length() - delimiter.length()); } else { ret.setLength(0); }
        return ret.toString();
    }

    private static boolean containsDefaultCsvSeparators(final String field) {
        return field.contains(",") ||
                field.contains(";") ||
                field.contains("\"") ||
                field.contains("\t");
    }

    public static String doubleQuotas(String src) {
        return "\""+src.replaceAll("\"","\"\"")+"\"";
    }


}

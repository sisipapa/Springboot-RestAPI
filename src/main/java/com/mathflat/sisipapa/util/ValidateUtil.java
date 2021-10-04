package com.mathflat.sisipapa.util;

import com.mathflat.sisipapa.enumtype.SchoolType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ValidateUtil {

    public static boolean studentNameValidate(String name) {

        boolean result = Pattern.matches("^[0-9a-zA-Z가-힣]*$", name);
        if(name.length() > 16) result = false;
        return result;
    }

    public static boolean studentAgeValidate(Integer age) {
        boolean result = true;
        if(age < 8) result = false;
        if(age > 19) result = false;
        return result;
    }

    public static boolean studentSchoolTypeValidate(SchoolType schoolType) {
        return Arrays.stream(SchoolType.values()).noneMatch(sType -> sType.name().equals(schoolType));
    }

    public static boolean studentPhoneNumberValidate(String phoneNumber) {
        return Pattern.matches("\\d{3}-\\d{4}-\\d{4}", phoneNumber);
    }

    public static boolean subjectNameValidate(String name) {

        boolean result = Pattern.matches("^[0-9a-zA-Z가-힣]*$", name);
        if(StringUtils.isEmpty(name)) result = false;
        if(name.length() > 12 || name.length() < 1) result = false;
        return result;

    }
}

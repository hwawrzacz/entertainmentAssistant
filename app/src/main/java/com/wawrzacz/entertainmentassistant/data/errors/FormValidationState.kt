package com.wawrzacz.entertainmentassistant.data.errors

enum class FormValidationState(var value: String) {
    LOGIN_TOO_SHORT("Email must contain > 5 digits"),
    EMAIL_EMPTY("Email cannot be empty"),
    FIELD_EMPTY("Field cannot be empty"),
    EMAIL_INVALID_CHARACTERS("Email contains invalid characters"),
    FIELD_INVALID_CHARACTERS("Field contains invalid characters"),
    FIELD_MUST_BE_NUMBER("Field must be number"),
    FIELD_REQUIRED("Field is required"),
    INCORRECT_CHARACTERS_FOR_NAMES_LIST("Field can only contains letters, comas, and space"),
    YEAR_BEFORE_CINEMATOGRAPHY("There were no movie before 1895"),
    NO_A_VALID_EMAIL("Provided string does not match an email pattern"),
    PASSWORD_EMPTY("Password cannot be empty"),
    PASSWORD_TOO_SHORT("Password must contain > 5 digits"),
    PASSWORDS_DONT_MATCH("Passwords don't match"),
    NOT_INITIALIZED(""),
    VALID("VALID")
}

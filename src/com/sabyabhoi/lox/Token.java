package com.sabyabhoi.lox;

public record Token(TokenType type, String lexeme, Object literal, int line) {
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}

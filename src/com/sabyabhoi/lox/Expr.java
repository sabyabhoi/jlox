package com.sabyabhoi.lox;

abstract public class Expr {
    static record Binary(Expr left, Token operator, Expr right) { }
}

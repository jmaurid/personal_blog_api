package com.binarybrains.blogpersonal.utils;

@FunctionalInterface
public interface Mapper<I, O> {
  O map(I input);
}

package com.daoyou.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class MapperSqlSyntaxTest {

 private static final Path MAPPER_DIRECTORY = Path.of("src/main/resources/mapper");
 private static final Pattern ATTACHED_LIMIT = Pattern.compile(
 "\\bLIMIT(?=(?:#\\{|\\$\\{|\\d|\\())",
 Pattern.CASE_INSENSITIVE);

 @Test
 void limitKeywordIsSeparatedFromItsArgument() throws IOException {
 List<String> invalidMappers;
 try (Stream<Path> paths = Files.walk(MAPPER_DIRECTORY)) {
 invalidMappers = paths.filter(path -> path.toString().endsWith(".xml"))
 .filter(MapperSqlSyntaxTest::containsAttachedLimit)
 .map(Path::toString)
 .sorted()
 .toList();
 }

 assertFalse(!invalidMappers.isEmpty(), () -> "LIMIT must be followed by whitespace: " + invalidMappers);
 }

 private static boolean containsAttachedLimit(Path path) {
 try {
 return ATTACHED_LIMIT.matcher(Files.readString(path)).find();
 } catch (IOException exception) {
 throw new IllegalStateException("Failed to read mapper: " + path, exception);
 }
 }
}

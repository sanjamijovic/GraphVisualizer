package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface Parser {
    Graph parseFile(File file) throws IOException, IllegalFileException;
}

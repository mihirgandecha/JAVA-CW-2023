package edu.uob;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;

// 1. list for checking if location is true
//
public class GameEntityState { // extends GameEntity - d.set for storing entities (furniture, character,
                               // artefacts)

  public GameEntityState(File entitiesFile) throws Exception {

  }

  boolean isFilePresent() {
    return true;
  }

  void setParser() throws ParseException {
  }

}

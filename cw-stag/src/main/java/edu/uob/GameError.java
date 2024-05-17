package edu.uob;
import java.io.Serial;

public class GameError extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;

        public GameError(String message){
            super("[ERROR]" + message);
        }
    }

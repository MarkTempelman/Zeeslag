module seabattleclient {
  opens APILoginREST;
  requires slf4j.api;
  requires javafx.graphics;
  requires javafx.controls;
    requires gson;
    requires java.sql;

    exports seabattlegui;
}
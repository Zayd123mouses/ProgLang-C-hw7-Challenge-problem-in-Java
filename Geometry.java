import java.util.ArrayList;

abstract public class Geometry{

    abstract GeometryValue eval_prog(ArrayList<Pair> env);
  
    abstract Geometry  preprocess_prog();

} 
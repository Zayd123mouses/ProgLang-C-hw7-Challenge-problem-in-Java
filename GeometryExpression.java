import java.util.ArrayList;

abstract public class GeometryExpression extends Geometry {
    public   static double Epsilon = 0.00001; 
    
}

 class Intersect extends GeometryExpression{

    public Geometry e1;
    public Geometry e2;

    public Intersect(Geometry e1, Geometry e2){
    this.e1 = e1;
    this.e2 = e2;
    }
     
    public Geometry preprocess_prog(){
        return new Intersect(e1.preprocess_prog(), e2.preprocess_prog());
    }
    public GeometryValue eval_prog(ArrayList<Pair> env){
        return e1.eval_prog(env).intersect(e2.eval_prog(env));
    }
}


class Let extends GeometryExpression{
    public String s;
    public Geometry e1;
    public Geometry e2;

    public Let(String s, Geometry e1, Geometry e2){
      this.s = s;
      this.e1 = e1;
      this.e2 = e2;
    }
  public Geometry preprocess_prog(){
    return new Let(s,e1.preprocess_prog(),e2.preprocess_prog());
  }
// Better to use Deque than ArrayList(). 
//  O(1) in deque
  public GeometryValue eval_prog(ArrayList<Pair> env){
    env.add(0,new Pair(s, e1.eval_prog(env)));
    return e2.eval_prog(env);
  }
     
}


class Var extends GeometryExpression{
    String s;
    public Var(String s){
        this.s = s;
    }

    public Geometry preprocess_prog(){
        return this;
    }

    public GeometryValue eval_prog(ArrayList<Pair> env){
        for(Pair p : env){
            if(p.key().equals(s)){
                return p.value();
            } 
        }
    throw new java.lang.RuntimeException("undefined variable");
 
    }


}


class Shift extends GeometryExpression{
    double dx,  dy;
    Geometry e;
    public Shift(double dx, double dy, Geometry e){
     this.dx = dx;
     this.dy = dy;
     this.e = e;
    }

    public Geometry preprocess_prog(){
        return new Shift(dx,dy, e.preprocess_prog());
    }

    public GeometryValue eval_prog(ArrayList<Pair> env){
        return e.eval_prog(env).shift(dx,dy);
    }
}

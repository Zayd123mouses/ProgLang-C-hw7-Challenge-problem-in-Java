import java.lang.Math;
import java.util.ArrayList;

abstract class GeometryValue extends Geometry{
 
    private boolean real_close(double r1,double r2){
        return Math.abs(r1 - r2) < GeometryExpression.Epsilon;
    }

    private boolean real_close_point(double x1, double y1, double x2, double y2){
        return real_close(x1, x2) && real_close(y1, y2);
    }
    private GeometryValue two_points_to_line(double x1, double y1, double x2, double y2){
        if (real_close(x1, x2)){
            return new VerticalLine(x1);
        }else{
            double m = (y2 - y1) / (x2 - x1);
            double b = y1 - m * x1;
            return new Line(m,b);
        }
    }
   

    public boolean call_real_close(double r1, double r2){
        return real_close(r1, r2);
    }
    public boolean call_close_points(double x1, double y1, double x2, double y2){
        return real_close_point(x1, y1, x2, y2);
    }

  public  GeometryValue eval_prog(ArrayList<Pair> env){
   return  this;
  }

  abstract GeometryValue intersect(GeometryValue other);

  abstract GeometryValue intersectPoint(Point p);
  
  abstract GeometryValue intersectLine(Line l);

  abstract GeometryValue intersectVerticalLine(VerticalLine vl);

  abstract GeometryValue intersectWithSegmentAsLineResult(LineSegment seg);

  abstract GeometryValue shift(double dx, double dy);

  public GeometryValue intersectNoPoint(NoPoint np){
    return np;
  }


 public Geometry preprocess_prog(){
    return this;
 }


    public GeometryValue intersectLineSegment(LineSegment seg){
        GeometryValue line_result = intersect(two_points_to_line(seg.x1,seg.y1,seg.x2,seg.y2));
        return line_result.intersectWithSegmentAsLineResult(seg);
    }
}

class NoPoint extends GeometryValue{
  
    public GeometryValue intersect(GeometryValue other){
        return other.intersectNoPoint(this); 
      } 

      public GeometryValue intersectPoint(Point p){
        return this;
      }

  public GeometryValue intersectLine(Line l){
    return this;
  }
  
  public GeometryValue intersectVerticalLine(VerticalLine vl){
    return this;
  }

  public GeometryValue intersectWithSegmentAsLineResult(LineSegment seg){
    return this;
  }

  public GeometryValue shift(double dx, double dy){
    return this;
  } 

}





class Point extends GeometryValue{
    private double x;
    private double y;
    public Point(double x, double y){
         this.x = x;
         this.y = y;
    }
    public double x(){
        return x;
    }
    public double y(){
        return y;
    }

 public GeometryValue intersect(GeometryValue other){
   return other.intersectPoint(this); 
 }    
 
 public GeometryValue intersectPoint(Point p){
    if(this.call_close_points(this.x(), this.y(), p.x(), p.y()))
    {
        return p;
    } else {
        return new NoPoint();
    }
 }

 public  GeometryValue intersectLine(Line l){
    return l.intersectPoint(this);
 }

 public GeometryValue intersectVerticalLine(VerticalLine vl){
    return vl.intersectPoint(this);
 }

 public GeometryValue intersectWithSegmentAsLineResult(LineSegment seg){
    if (inbetween(this.x(), seg.x1, seg.x2) &&  inbetween(this.y(),seg.y1,seg.y2)){
        return new Point(x(),y());
    } else{
        return new NoPoint();
    }
 }

 public GeometryValue shift(double dx, double dy){
    return new Point(x() + dx , y() + dy);
 }

 private Boolean inbetween(double v, double end1, double end2){
    double epsilon = GeometryExpression.Epsilon;
    return ((end1 - epsilon <= v && v <= end2 + epsilon) || (end2 - epsilon <= v && v <= end1 + epsilon));
 }

}


class Line extends GeometryValue{
    public double m;
    public double b;
    public Line(double m, double b){
        this.m = m;
        this.b = b;
    }

    public GeometryValue intersect(GeometryValue other){
        return other.intersectLine(this);
    }

    public GeometryValue intersectPoint(Point p){
        if (this.call_real_close(p.y(), (m * p.x()) + b)){
            return p;
        } else {
            return new NoPoint();
        }
    }

    public GeometryValue intersectLine(Line l){
        if(this.call_real_close(m, l.m)){
            if(this.call_real_close(b, l.b)){
                return l;
            }else{
                return new NoPoint();
            }
        } else{
            double x = (b - l.b) / (l.m - m);
            return new Point(x, (l.m * x) + l.b);
        }
    }

    public GeometryValue intersectVerticalLine(VerticalLine vl){
        return vl.intersectLine(this);
    }

 public GeometryValue intersectWithSegmentAsLineResult(LineSegment seg){
    return seg;
 }
 
 public GeometryValue shift(double dx, double dy){
    return new Line(m, (b + dy) - (m * dx));
 }

}

class VerticalLine extends GeometryValue{
    public double x;
    public VerticalLine(double x){
        this.x = x;
    }
  
   public GeometryValue intersect(GeometryValue other){
   return  other.intersectVerticalLine(this);
   }

   public GeometryValue intersectPoint(Point p){
    if (this.call_real_close(p.x(), x)){
        return p;
    }else{
        return new NoPoint();
    }
   }

   public GeometryValue intersectVerticalLine(VerticalLine vl){
    if (this.call_real_close(vl.x, x)){
        return vl;
    }else{
        return new NoPoint();
    }
   }

   public GeometryValue intersectLine(Line l){
    return new Point(x, (l.m * x) + l.b);
   }

   
 public GeometryValue intersectWithSegmentAsLineResult(LineSegment seg){
    return seg;
 }

 public GeometryValue shift(double dx, double dy){
   return  new VerticalLine(x + dx);
 }
   
}

class LineSegment extends GeometryValue{
    public double x1;
    public double y1;
    public double x2;
    public double y2;

    public LineSegment(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

    }
    // override preprocess prog
     @Override
     public GeometryValue preprocess_prog(){
        if(this.call_close_points(x1, y1, x2, y2)){
            return new Point(x1,y1);
        } else if ((x1 < x2) || (y1 < y2)){
            return new LineSegment(x1, y1, x2, y2);
        } else {
            return new LineSegment(x2, y2, x1, y1).preprocess_prog();
        }
     }



    public GeometryValue intersect(GeometryValue other){
        return other.intersectLineSegment(this);
    } 

    public GeometryValue intersectPoint(Point p){
        return p.intersectLineSegment(this);
    }

    public GeometryValue intersectLine(Line l){
        return l.intersectLineSegment(this);
    }

    
    public GeometryValue intersectVerticalLine(VerticalLine vl){
        return vl.intersectLineSegment(this);
    }

    public GeometryValue intersectWithSegmentAsLineResult(LineSegment seg){
        if(this.call_real_close(seg.x1, seg.x2)){
            return comput1(seg);
        } else {
            return comput2(seg);
        }
    }

    public GeometryValue shift(double dx,  double dy){
        return new LineSegment(x1 + dx, y1 + dy, x2 + dx, y2 + dy);
    }
    


    private GeometryValue comput1(LineSegment seg){
        double aXend,aYend;
        double bXstart,bYstart,bXend,bYend;
        if (seg.y1 < this.y1){
         aXend = seg.x2;
         aYend = seg.y2;
         bXstart = this.x1;
         bYstart = this.y1;
         bXend = this.x2;
         bYend = this.y2;
        }else{
            aXend = this.x2;
            aYend = this.y2;
            bXstart = seg.x1;
            bYstart = seg.y1;
            bXend = seg.x2;
            bYend = this.y2;

        }
        if(seg.call_real_close(aYend,bYstart)){
            return new Point(aXend,aYend);
        } else if(aYend < bYstart){
            return new NoPoint();
        } else if(aYend > bYend){
          return  new LineSegment(bXstart,bYstart,bXend,bYend);
        } else {
            return  new LineSegment(bXstart,bYstart,aXend,aYend);
        }

    }

 private GeometryValue comput2(LineSegment seg){
        double aXend,aYend;
        double bXstart,bYstart,bXend,bYend;
        if (seg.x1 < this.x1){
         aXend = seg.x2;
         aYend = seg.y2;
         bXstart = this.x1;
         bYstart = this.y1;
         bXend = this.x2;
         bYend = this.y2;
        }else{
            aXend = this.x2;
            aYend = this.y2;
            bXstart = seg.x1;
            bYstart = seg.y1;
            bXend = seg.x2;
            bYend = this.y2;

        }

        if(this.call_real_close(aXend, bXstart)){
            return new Point(aXend,aYend);
        } else if (aXend < bXstart){
            return new NoPoint();
        } else if (aXend > bXend ){
            return  new LineSegment(bXstart,bYstart,bXend,bYend);
        } else {
            return  new LineSegment(bXstart,bYstart,aXend,aYend);

        }

    }

}
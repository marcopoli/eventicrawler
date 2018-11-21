package utility;

import java.util.Vector;



public class SimilaritaCoseno 
{
	public static double calculateCosine(Vector<Double> a, Vector<Double> b) {

		  double p = 0.0;
		  double na = 0.0;
		  double nb = 0.0;

		  int i;
		  for (i = 0; i < a.size(); i++) {
		    p = p + (a.get(i) * b.get(i));
		    na = na+ ( a.get(i) * a.get(i));
		    nb = nb +( b.get(i) * b.get(i));
		  }

		  return  (p / (Math.sqrt(na) * Math.sqrt(nb)));

        
    }
    
    public static float calculateTanimoto(Vector<Double> features1, Vector<Double> features2) {

    	
        int n = features1.size();
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;

        for (int i = 0; i < n; i++) {
            ab += features1.get(i) * features2.get(i);
            a2 += features1.get(i)*features1.get(i);
            b2 += features2.get(i)*features2.get(i);
        }
        return (float)ab/(float)(a2+b2-ab);
    }

}

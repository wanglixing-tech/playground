package crs.fcl.integration.main;

public class Launcher {
    public static void main(String[] args) throws Exception {
        if (args != null && args.length > 0) {
            String option= args[0]; 
            String[] args2=new String[0];               

            if( args.length>1){
                args2= new String[args.length-1];
                System.arraycopy(args, 1, args2, 0, args2.length); 
            }

            if(option.equals("a"))                 
                ClassA.main(args2);
            else if(option.equals("b"))                
               ClassB.main(args2);
        }
    }
}

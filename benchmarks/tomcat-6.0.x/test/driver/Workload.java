package driver;


public class Workload {
	final static String[] SCRIPT={
	"examples/servlets/servlet/HelloWorldExample",
	"examples/servlets/servlet/RequestInfoExample",
	"examples/servlets/servlet/RequestHeaderExample",
	"examples/servlets/servlet/RequestParamExample",
	"examples/servlets/servlet/CookieExample",
	"examples/servlets/servlet/SessionExample",
	"examples/jsp/jsp2/el/basic-arithmetic.jsp",
	"examples/jsp/jsp2/el/basic-comparisons.jsp",
	"examples/jsp/jsp2/el/implicit-objects.jsp?foo=bar",
	"examples/jsp/jsp2/el/functions.jsp?foo=JSP+2.0",
	"examples/jsp/jsp2/simpletag/hello.jsp",
	"examples/jsp/jsp2/simpletag/repeat.jsp",
	"examples/jsp/jsp2/simpletag/book.jsp",
	"examples/jsp/jsp2/jspx/svgexample.html",
	"examples/jsp/jsp2/jspattribute/jspattribute.jsp",
	"examples/jsp/num/numguess.jsp",
	"examples/jsp/tagplugin/if.jsp"};
	
	final static int[][] stateTrans={
		{1,3,12,14},
		{2,10,8,14},
		{15},
		{4,6,7,14},
		{5,14},
		{3,14},
		{3,14},
		{3,14},
		{9,14},
		{1,14},
		{11,14},
		{1,14},
		{13,14},
		{1,14},
		{},
		{16},
		{1,14}
	};
	final static double[][] stateProb={
		{0.32,0.64,0.72,0.799999},
		{0.56,0.64,0.72,0.799999},
		{1.0},
		{0.08,0.559999,0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{0.72,0.799999},
		{},
		{1.0},
		{0.72,0.799999}
	};
	
	public static int THINKTIME=10;
}

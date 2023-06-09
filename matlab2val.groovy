public class Matlab2Val {
public static void main(String [] args) {

def input = null
def output = null

def opt = ['-in': { input = it}, '-out': { output = it }]

opt.each { k, v ->
    def index = args.toList().indexOf(k)
    if(index != -1 && (index + 1) < args.size()) {
        v(args[index+1])
    }
}

if(input == null) throw new Exception("use -in <file> to specfied a input file")
if(output == null) throw new Exception("use -out <file> to specfied a output file")

def f = new File(input)
def out = new File(output)

def n = 12
def H = 2
def i = 0
def j = 0
out.withWriter { w ->
    f.eachLine { l ->
        w << "(${i},${j},${H}) = ${l}\n"
        j++
        if(j >= n) {
            j = 0
            i++
        }
    }
}
}
}
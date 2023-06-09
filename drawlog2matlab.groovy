public class Drawlog2Matlab {
public static void main(String [] args) {

def file = null
def n = null
def dt = null
def dy = null
def dx = null
def g= null

def opt = ['-n': { n = it.toInteger()}, '-drw': { file = it }, '-dt': { dt = it?.toBigDecimal() }, '-dx': { dx = it?.toBigDecimal()}, '-dy': {dy = it?.toBigDecimal()}, '-g': {g = it?.toBigDecimal()}]

opt.each { k, v ->
    def index = args.toList().indexOf(k)
    if(index != -1 && (index + 1) < args.size()) {
        v(args[index+1])
    }
}

if(file == null) throw new Exception("use -drw to specfied a input file")
if(n == null) throw new Exception("use -n to specfied a the size of the grid")
if(dt == null) throw new Exception("use -dt to specfied delta t")
if(dx == null) throw new Exception("use -dx to specfied delta x")
if(dy == null) throw new Exception("use -dy to specfied delta y")
if(g == null) throw new Exception("use -g to specfied gravity constant")

def toM = { source, rows, cols, index, name ->
    def p = java.util.regex.Pattern.compile('(?:[^\\|]*\\|){' + (index * 2 - 1) + '}([^\\|]+).*')
    def rs = []
    def f = new File(source)
    if(!f.exists()) {
        f = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + file)
        if(!f.exists()) {
            throw new Exception("${file} not found!")
        }
    }
    f.eachLine {
        def m = p.matcher(it)
        if(m.matches()) {
            rs << m.group(1)
       }
    }
    def steps = Math.floor(rs.size()/rows).toInteger()
    println "${name} = zeros(${rows},${cols},${steps});"
    rs.eachWithIndex { r, i ->
        if(i % rows == 0) {
            println "${name}(:,:,${(i/rows).toInteger()+1}) = ["
        }
        println "${r};"
        if((i+1) % rows == 0) {
            println "];"
        }
    }
    return steps
}
println """
function TP2
clf
shg
set(gcf,'numbertitle','off','name','TP2')
x = (0:${n}-1)/(${n}-1);
scrsz = get(0,'ScreenSize');
scrsz = [scrsz(3)*0.1 scrsz(4)*0.1 scrsz(3)*0.8 scrsz(4)*0.8];
set(gcf,'Position',scrsz);
sa = subplot(1,2,1);
ma = subplot(1,2,2);
surfplot = surf(sa,x,x,ones(${n},${n}),zeros(${n},${n}));
surfplot2 = surf(ma,x,x,ones(${n},${n}),zeros(${n},${n}));

axis([sa ma], [0 1 0 1 -1 3], 'square')
dt = ${dt};
dx = ${dx};
dy = ${dy};
g = ${g};
caxis(sa,[-1 1])
caxis(ma,[-1 1])
shading faceted
c = (1:${n})'/${n};
cyan = [0*c c c];
colormap(sa,cyan)
colormap(ma,cyan)
title(sa, 'Simulated');
title(ma, 'Matlab');

n = ${n};

H = ones(n+2,n+2);   U = zeros(n+2,n+2);  V = zeros(n+2,n+2);
Hx = zeros(n+1,n+1); Ux = zeros(n+1,n+1); Vx = zeros(n+1,n+1);
Hy = zeros(n+1,n+1); Uy = zeros(n+1,n+1); Vy = zeros(n+1,n+1);

"""
def minSteps = [toM(file, n+2, n+2, 3, "H1"),
toM(file, n+2, n+2, 2, "U1"),
toM(file, n+2, n+2, 1, "V1")].min()

println """

H = H1(:,:,1);

steps = ${minSteps};
u = 2:${n}+1;
k = 2:${n}+1; 
step = 0;

while step < steps
    step = step + 1;
    if mod(step,2) == 1 
       % First half step
   
       % x direction
       i = 1:${n}+1;
       j = 1:${n};
   
       % height
       Hx(i,j) = (H(i+1,j+1)+H(i,j+1))/2 - dt/(2*dx)*(U(i+1,j+1)-U(i,j+1));
   
       % x momentum
       Ux(i,j) = (U(i+1,j+1)+U(i,j+1))/2 -  ...
                 dt/(2*dx)*((U(i+1,j+1).^2./H(i+1,j+1) + g/2*H(i+1,j+1).^2) - ...
                            (U(i,j+1).^2./H(i,j+1) + g/2*H(i,j+1).^2));
   
       % y momentum
       Vx(i,j) = (V(i+1,j+1)+V(i,j+1))/2 - ...
                 dt/(2*dx)*((U(i+1,j+1).*V(i+1,j+1)./H(i+1,j+1)) - ...
                            (U(i,j+1).*V(i,j+1)./H(i,j+1)));
       
       % y direction
       i = 1:n;
       j = 1:n+1;
   
       % height
       Hy(i,j) = (H(i+1,j+1)+H(i+1,j))/2 - dt/(2*dy)*(V(i+1,j+1)-V(i+1,j));
   
       % x momentum
       Uy(i,j) = (U(i+1,j+1)+U(i+1,j))/2 - ...
                 dt/(2*dy)*((V(i+1,j+1).*U(i+1,j+1)./H(i+1,j+1)) - ...
                            (V(i+1,j).*U(i+1,j)./H(i+1,j)));
       % y momentum
       Vy(i,j) = (V(i+1,j+1)+V(i+1,j))/2 - ...
                 dt/(2*dy)*((V(i+1,j+1).^2./H(i+1,j+1) + g/2*H(i+1,j+1).^2) - ...
                            (V(i+1,j).^2./H(i+1,j) + g/2*H(i+1,j).^2));
   
       % Second half step
       i = 2:n+1;
       j = 2:n+1;
   
       % height
       H(i,j) = H(i,j) - (dt/dx)*(Ux(i,j-1)-Ux(i-1,j-1)) - ...
                         (dt/dy)*(Vy(i-1,j)-Vy(i-1,j-1));
       % x momentum
       U(i,j) = U(i,j) - (dt/dx)*((Ux(i,j-1).^2./Hx(i,j-1) + g/2*Hx(i,j-1).^2) - ...
                         (Ux(i-1,j-1).^2./Hx(i-1,j-1) + g/2*Hx(i-1,j-1).^2)) ...
                       - (dt/dy)*((Vy(i-1,j).*Uy(i-1,j)./Hy(i-1,j)) - ...
                         (Vy(i-1,j-1).*Uy(i-1,j-1)./Hy(i-1,j-1)));
       % y momentum
       V(i,j) = V(i,j) - (dt/dx)*((Ux(i,j-1).*Vx(i,j-1)./Hx(i,j-1)) - ...
                         (Ux(i-1,j-1).*Vx(i-1,j-1)./Hx(i-1,j-1))) ...
                       - (dt/dy)*((Vy(i-1,j).^2./Hy(i-1,j) + g/2*Hy(i-1,j).^2) - ...
                         (Vy(i-1,j-1).^2./Hy(i-1,j-1) + g/2*Hy(i-1,j-1).^2));
   
    
    C1 = abs(U(i,j)) + abs(V(i,j));  % Color shows momemtum
    set(surfplot2,'zdata',H(i,j),'cdata',C1);

    C = abs(U1(u,k,step)) + abs(V1(u,k,step));
    set(surfplot,'zdata',H1(u,k,step),'cdata',C);

    drawnow;
    end
end
close(gcf)
end
"""
}
}
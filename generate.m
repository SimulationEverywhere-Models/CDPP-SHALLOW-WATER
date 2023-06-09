% genera un archivo con valores de H iniciales con un perturbacion inicial
% n : tamanio de la grilla
% h, w: tamanio de la perturbacion, tamanio de la base y altura
% f : archivo de salida
% requiere pasarlo por el matlab2val para generar un archivo val valido
function H = generate(n,h,w,f)
H = ones(n+2,n+2);
D = droplet(h,w);
w = size(D,1);
i = ceil(rand*(n-w))+(1:w);
j = ceil(rand*(n-w))+(1:w);
H(i,j) = H(i,j) + rand*D;

function D = droplet(height,width)
% DROPLET  2D Gaussian
% D = droplet(height,width)
   [x,y] = ndgrid(-1:(2/(width-1)):1);
   D = height*exp(-5*(x.^2+y.^2));

end

save(f,'H','-ASCII');
end

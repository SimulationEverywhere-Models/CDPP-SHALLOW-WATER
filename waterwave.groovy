public class Waterwave {
public static void main(String [] args) {

def size = 10
def delay = 50
def defaultDelay = 100
def dt = 1/defaultDelay
def dx = 1.0
def dy = 1.0
def gravity = 9.8
def name = 'waterwave'

def help = {->
    println """
Generates a .ma and a .inc files for waterwave simulation. Will require a .val file with the same name of the generated .ma.
options:
    -h/-help      : display this help
    -size         : size of the grid, default ${size}
    -delay        : half step delay, default ${delay}
    -defaultDelay : default delay, default ${defaultDelay}
    -dt           : dt, default ${dt}
    -dx           : dx, default ${dx}
    -dy           : dy, default ${dy}
    -gravity      : gravity constant, default ${gravity}
    -name         : name of the files, default ${name}<size>.ma and ${name}<size>.inc
    """
}

def showHelp = false

def opt = ['-size': { size = it?.toInteger()}, 
        '-h': { showHelp = true }, 
        '-help': { showHelp = true },
        '-delay': { delay = it?.toInteger()},
        '-defaultDelay': { defaultDelay = it?.toInteger()},
        '-dt': { size = it?.toInteger()},
        '-dx': { size = it?.toInteger()},
        '-dy': { size = it?.toInteger()},
        '-gravity': { gravity = it?.toInteger()},
        '-name': { name = it}
]

opt.each { k, v ->
    def index = args.toList().indexOf(k)
    if(index != -1) {
        if((index + 1) < args.size()) {
            v(args[index+1])
        } else {
            v()
        }
    }
}

if(!(size && delay && defaultDelay && dt && dx && dy && gravity && name && !showHelp)) {
    println 'some invalid or missing option'
    help()
    return
}

def size2 = size + 2
def size1 = size - 1

def fileName = {-> name != 'waterwave' ? name : name+size}

def inc = """
#BeginMacro(delay)
${delay}
#EndMacro

#BeginMacro(default_delay)
${defaultDelay}
#EndMacro

#BeginMacro(dt)
${dt}
#EndMacro

#BeginMacro(dx)
${dx}
#EndMacro

#BeginMacro(dy)
${dy}
#EndMacro

#BeginMacro(gravedad)
${gravity}
#EndMacro

"""
def ma = """
#include(${fileName()}.inc)

[top]
components : waterwave

[waterwave]
type : cell
dim : (${size2},${size2},10) % HalfStepFlag H U V Hx Ux Vx Hy Uy Vy
border : wrapped
delay : transport
defaultDelayTime : #macro(default_delay)
neighbors : waterwave(1,1,0) waterwave(1,0,0) waterwave(1,-1,0) waterwave(0,1,0) waterwave(0,0,0) waterwave(0,-1,0) waterwave(-1,-1,0) waterwave(-1,0,0) waterwave(-1,1,0)
neighbors : waterwave(1,1,1) waterwave(1,0,1) waterwave(1,-1,1) waterwave(0,1,1) waterwave(0,0,1) waterwave(0,-1,1) waterwave(-1,-1,1) waterwave(-1,0,1) waterwave(-1,1,1)
neighbors : waterwave(1,1,2) waterwave(1,0,2) waterwave(1,-1,2) waterwave(0,1,2) waterwave(0,0,2) waterwave(0,-1,2) waterwave(-1,-1,2) waterwave(-1,0,2) waterwave(-1,1,2)
neighbors : waterwave(1,1,3) waterwave(1,0,3) waterwave(1,-1,3) waterwave(0,1,3) waterwave(0,0,3) waterwave(0,-1,3) waterwave(-1,-1,3) waterwave(-1,0,3) waterwave(-1,1,3)
neighbors : waterwave(1,1,4) waterwave(1,0,4) waterwave(1,-1,4) waterwave(0,1,4) waterwave(0,0,4) waterwave(0,-1,4) waterwave(-1,-1,4) waterwave(-1,0,4) waterwave(-1,1,4)
neighbors : waterwave(1,1,5) waterwave(1,0,5) waterwave(1,-1,5) waterwave(0,1,5) waterwave(0,0,5) waterwave(0,-1,5) waterwave(-1,-1,5) waterwave(-1,0,5) waterwave(-1,1,5)
neighbors : waterwave(1,1,6) waterwave(1,0,6) waterwave(1,-1,6) waterwave(0,1,6) waterwave(0,0,6) waterwave(0,-1,6) waterwave(-1,-1,6) waterwave(-1,0,6) waterwave(-1,1,6)
neighbors : waterwave(1,1,7) waterwave(1,0,7) waterwave(1,-1,7) waterwave(0,1,7) waterwave(0,0,7) waterwave(0,-1,7) waterwave(-1,-1,7) waterwave(-1,0,7) waterwave(-1,1,7)
neighbors : waterwave(1,1,8) waterwave(1,0,8) waterwave(1,-1,8) waterwave(0,1,8) waterwave(0,0,8) waterwave(0,-1,8) waterwave(-1,-1,8) waterwave(-1,0,8) waterwave(-1,1,8)
neighbors : waterwave(1,1,9) waterwave(1,0,9) waterwave(1,-1,9) waterwave(0,1,9) waterwave(0,0,9) waterwave(0,-1,9) waterwave(-1,-1,9) waterwave(-1,0,9) waterwave(-1,1,9)
initialValue : 0
initialCellsValue : ${fileName()}.val
localTransition : none
zone : HalfStep { (1,1,9)..(${size},${size},9) }
zone : Hx { (0,0,8)..(${size},${size1},8) }
zone : Ux { (0,0,7)..(${size},${size1},7) }
zone : Vx { (0,0,6)..(${size},${size1},6) }
zone : Hy { (0,0,5)..(${size1},${size},5) }
zone : Uy { (0,0,4)..(${size1},${size},4) }
zone : Vy { (0,0,3)..(${size1},${size},3) }
zone : H { (1,1,2)..(${size},${size},2) }
zone : U { (1,1,1)..(${size},${size},1) }
zone : V { (1,1,0)..(${size},${size},0) }

[HalfStep]
rule : { 1 } #macro(delay) { (0,0,0) = 0 }
rule : { 0 } #macro(delay) { (0,0,0) = 1 }

[Hx] % H=4 U=3 HalfStep=1
rule : { ( (1,1,4) + (0,1,4) ) / 2 - #macro(dt) / ( 2 * #macro(dx) ) * ( (1,1,3) - (0,1,3) ) } #macro(delay) { (0,0,1) = 0 }
rule : { (0,0,0) } #macro(delay) { t }

[Ux] % H=5 U=4 HalfStep=2
rule : { ( (1,1,4) + (0,1,4) ) / 2 - #macro(dt) / ( 2 * #macro(dx) ) * ( ( ( (1,1,4) * (1,1,4) ) / (1,1,5) + #macro(gravedad) / 2 * ( (1,1,5) * (1,1,5) ) ) - ( ( (0,1,4) * (0,1,4) ) / (0,1,5) + #macro(gravedad) / 2 * ( (0,1,5) * (0,1,5) ) ) ) } #macro(delay) { (0,0,2) = 0 }
rule : { (0,0,0) } #macro(delay) { t }

[Vx] % H=6 U=5 V=4 HalfStep=3
rule : { ( (1,1,4) + (0,1,4) ) / 2 - #macro(dt) / ( 2 * #macro(dx) ) * ( ( (1,1,5) * (1,1,4) / (1,1,6) ) - ( (0,1,5) * (0,1,4) / (0,1,6) ) ) } #macro(delay) { (0,0,3) = 0 }
rule : { (0,0,0) } #macro(delay) { t }

[Hy] % H=7 V=5 HalfStep=4
rule : { ( (1,1,7) + (1,0,7) ) / 2 - #macro(dt) / ( 2 * #macro(dy) ) * ( (1,1,5) - (1,0,5) ) } #macro(delay) { (0,0,4) = 0 }
rule : { (0,0,0) } #macro(delay) { t }

[Uy] % H=8 U=7 V=6 HalfStep=5
rule : { ( (1,1,7) + (1,0,7) ) / 2 - #macro(dt) / ( 2 * #macro(dy) ) * ( ( (1,1,6) * (1,1,7) / (1,1,8) ) - ( (1,0,6) * (1,0,7) / (1,0,8) ) ) } #macro(delay) { (0,0,5) = 0 }
rule : { (0,0,0) } #macro(delay) { t }

[Vy] % H=9 V=7 HalfStep=6
rule : { ( (1,1,7) + (1,0,7) ) / 2 - #macro(dt) / ( 2 * #macro(dy) ) * ( ( ( (1,1,7) * (1,1,7) ) / (1,1,9) + #macro(gravedad) / 2 * ( (1,1,9) * (1,1,9) ) ) - ( ( (1,0,7) * (1,0,7) ) / (1,0,9) + #macro(gravedad) / 2 * ( (1,0,9) * (1,0,9) ) ) ) } #macro(delay) { (0,0,6) = 0 }
rule : { (0,0,0) } #macro(delay) { t }

[H] % H=0 Ux=5 Vy=1 HalfStep=7
rule : { (0,0,0) - ( #macro(dt) / #macro(dx) ) * ( (0,-1,5) - (-1,-1,5) ) - ( #macro(dt) / #macro(dy) ) * ( (-1,0,1) - (-1,-1,1) ) } #macro(delay) { (0,0,7) = 1 }
rule : { (0,0,0) } #macro(delay) { t }

[U] % U=0 Hx=7 Ux=6 Hy=4 Uy=3 Vy=2 HalfStep=8
rule : { (0,0,0) - ( #macro(dt) / #macro(dx) ) * ( ( ( (0,-1,6) * (0,-1,6) ) / (0,-1,7) + #macro(gravedad) / 2 * ( (0,-1,7) * (0,-1,7) ) ) - ( ( (-1,-1,6) * (-1,-1,6) ) / (-1,-1,7) + #macro(gravedad) / 2 * ( (-1,-1,7) * (-1,-1,7) ) ) ) - ( #macro(dt) / #macro(dy) ) * ( ( (-1,0,2) * (-1,0,3) / (-1,0,4) ) - ( (-1,-1,2) * (-1,-1,3) / (-1,-1,4) ) ) } #macro(delay) { (0,0,8) = 1 }
rule : { (0,0,0) } #macro(delay) { t }

[V] % V=0 Hx=8 Ux=7 Vx=6 Hy=5 Vy=3 HalfStep=9
rule : { (0,0,0) - ( #macro(dt) / #macro(dx) ) * ( ( (0,-1,7) * (0,-1,6) / (0,-1,8) ) - ( (-1,-1,7) * (-1,-1,6) / (-1,-1,8) ) ) - ( #macro(dt) / #macro(dy) ) * ( ( ( (-1,0,3) * (-1,0,3) ) / (-1,0,5) + #macro(gravedad) / 2 * ( (-1,0,5) * (-1,0,5) ) ) - ( ( (-1,-1,3) * (-1,-1,3) ) / (-1,-1,5) + #macro(gravedad) / 2 * ( (-1,-1,5) * (-1,-1,5) ) ) ) } #macro(delay) { (0,0,9) = 1 }
rule : { (0,0,0) } #macro(delay) { t }

[none]
rule : { (0,0,0) } #macro(delay) { t }

"""

new File(fileName()+'.inc').withWriter {
    it << inc
}

new File(fileName()+'.ma').withWriter {
    it << ma
}

}
}
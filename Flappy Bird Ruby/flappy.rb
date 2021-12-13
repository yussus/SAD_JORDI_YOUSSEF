require 'gosu'
require_relative 'defstruct'
require_relative 'vector'

GRAVITY = Vec[0,800] #pixels/s2
JUMP_VEL= Vec[0,300]
OBSTACLE_SPEED=200
OBSTACLE_SPAWN=1.5
OBSTACLE_GAP=100
DEATH_VEL=Vec[20,-500]
DEATH_ROT_VEL=360#deg/sec

Rect = DefStruct.new{{
    pos: Vec[0,0],
    size: Vec[0,0],
  }}.reopen do
    def minx
        pos.x
    end
    def miny
        pos.y
    end
    def maxx
        pos.x + size.x
    end
    def maxy
        pos.y + size.y
    end
end

Obstacle = DefStruct.new{{
    pos: Vec[0,0],
    bird_cross: false,
    gap: OBSTACLE_GAP
  }}



class GameWindow < Gosu::Window #https://www.rubydoc.info/github/gosu/gosu/Gosu/Window
    def initialize(*arg)
        super
        @font=Gosu::Font.new(self, Gosu.default_font_name, 25) #clase de Gosu
        @images = { 
            background: Gosu::Image.new(self, 'images/background.png', false),
            base: Gosu::Image.new(self, 'images/base.png', false),
            bird: Gosu::Image.new(self, 'images/bird.png', false),
            obst: Gosu::Image.new(self, 'images/piper.png',false),
            flappy: Gosu::Image.new(self, 'images/flappybird.png',false)
        }#Hash més útil
        @sons = {
            flap: Gosu::Sample.new(self, 'sounds/wing.wav'),
            point: Gosu::Sample.new(self, 'sounds/point.wav'),
            die: Gosu::Sample.new(self, 'sounds/die.wav'),
            hit: Gosu::Sample.new(self,'sounds/hit.wav')
        }
        #@state = GameState.now # inclou ja inicialitzat el @scroll_x=0
        @marc=0
        @start=false
        @scroll_x = 0
        @bird_pos= Vec[35,200]
        @bird_vel= Vec[0,0]
        @bird_rot=0
        @obstacles= []
        @obstacle_cd_spawn=OBSTACLE_SPAWN
        @alive=true

    end

    def button_down(button)
        case button
        when Gosu::KbEscape
            then close
        when Gosu::KbSpace 
            if @alive
                @bird_vel.y -= JUMP_VEL.y
                @sons[:flap].play(0.5, rand(0.9..1.1))
            end #43:20
            @start=true
        when Gosu::KbR
            restart_game
        end
    end

    def restart_game
        @marc=0
        @start=false
        @scroll_x = 0
        @bird_pos= Vec[35,200]
        @bird_vel= Vec[0,0]
        @bird_rot=0
        @obstacles= []
        @obstacle_cd_spawn=OBSTACLE_SPAWN
        @alive=true
    end


    def update
        @scroll_x += (update_interval / 1000)*OBSTACLE_SPEED*0.5
        if @scroll_x >  @images[:base].width
            @scroll_x=0
        end

        return unless @start

        @bird_vel.y += GRAVITY.y * (update_interval / 1000) #44:00
        @bird_pos.y += @bird_vel.y * (update_interval / 1000)

        if @alive
            @obstacle_cd_spawn -= (update_interval / 1000)
            if @obstacle_cd_spawn <= 0
                @obstacles<<Obstacle.new(pos:Vec[width, rand(50..270)])
                @obstacle_cd_spawn += OBSTACLE_SPAWN
            end
        end

        @obstacles.each do |obstacled|
            obstacled.pos.x -= (update_interval / 1000)*OBSTACLE_SPEED
            if obstacled.pos.x < @bird_pos.x && !obstacled.bird_cross && @alive
                @marc += 1
                obstacled.bird_cross = true
                @sons[:point].play(0.5,1)
            end
        end
        
        @obstacles.reject! { |obst| obst.pos.x < -@images[:obst].width } #treure els obst de l'array quan surten de la pantalla

        if @alive && bird_coll?
            @alive=false
            @bird_vel.set!(DEATH_VEL)
            @sons[:die].play(0.5, 1)
        end
        unless @alive
            @bird_rot += (update_interval / 1000)*DEATH_ROT_VEL
        end
    end
    
    def bird_coll?
        bird_r=bird_rect
        return true if obstacle_rect.find{|obst_r| rectangle_intersect?(bird_r, obst_r)}
        not rectangle_intersect?(bird_r, Rect.new(pos: Vec[0, 0], size: Vec[width, height])) #colisio amb els bordes de la finestra
    end

    def rectangle_intersect?(br, obsr)
        if br.maxx < obsr.minx || br.minx > obsr.maxx || br.maxy < obsr.miny || br.miny > obsr.maxy
            return false
            @sons[:hit].play(0.5, 1)
        end
        true
    end

    def draw
        
        @images[:background].draw(0,0,0)
        @images[:base].draw(-@scroll_x,420,0)
        @images[:base].draw(-(@scroll_x - @images[:base].width),420,0)
        @obstacles.each do |obstacle|
            @images[:obst].draw(obstacle.pos.x, -@images[:obst].height+obstacle.pos.y , 0)
            scale(1, -1) do
                @images [:obst].draw(obstacle.pos.x, -@images[:obst].height - obstacle.pos.y - OBSTACLE_GAP, 0)
            end
        end
        @images[:bird].draw_rot(@bird_pos.x+(@images[:bird].width/2),@bird_pos.y+(@images[:bird].height/2),0, @bird_rot) #dibuixem l'ocell amb capacitat de rotacio
        @font.draw_rel(@marc.to_s, width/2.0, 60, 0, 0.5, 0.5) #dibuixa el marcador
        
        #debug_draw
    end

    def bird_rect
        bird_rect = Rect.new(
            pos: @bird_pos, 
            size:Vec[@images[:bird].width, @images[:bird].height]
        )
    end

    def obstacle_rect
       # top1 = Rect.new()
        @obstacles.flat_map do |obstacle|
            bottom = Rect.new(
                pos: Vec[obstacle.pos.x, obstacle.pos.y+ OBSTACLE_GAP ],
                size: Vec[@images[:obst].width, @images[:obst].height]
            )
            top = Rect.new(
                pos: Vec[obstacle.pos.x,  -@images[:obst].height+obstacle.pos.y],
                size: Vec[@images[:obst].width, @images[:obst].height]
            )
            base =Rect.new(
                pos: Vec[0,420],
                size: Vec[@images[:base].width, @images[:base].height]
            )
            #    top1=top
           # end#1,51,00
        [top, bottom, base]
        end
    end
    def debug_draw #utilitzat per comprobar que funciona la deteccio d'objectes
        color = bird_coll? ? Gosu::Color::RED : Gosu::Color::GREEN
        draw_debug_rect(bird_rect, color)
        obstacle_rect.each do |obstacles_rect|
            draw_debug_rect(obstacles_rect)
        end
    end

    def draw_debug_rect(rect, color = Gosu::Color::GREEN) #metode per dibuixar el rectangle que necessitem
        
        x = rect.pos.x
        y = rect.pos.y
        w = rect.size.x
        h = rect.size.y

        points = [
            Vec[x, y],
            Vec[x + w, y],
            Vec[x + w,y + h],
            Vec[x,y + h]
        ]

        points.each_with_index do |p1, idx|
            p2 = points[(idx + 1)% points.size]
            draw_line(p1.x, p1.y, color, p2.x, p2.y, color)
        end
    end
end

window = GameWindow.new(288,512,false)
window.show
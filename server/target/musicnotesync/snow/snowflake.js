/**
 * Created by michael on 12/6/16.
 */
class Snowflake{
    constructor(startx){
        this.x = startx;
        this.y = 0;
        this.speed = 2;
        this.stopped = false;
    }

    calcNextPosition(){
        var direction = Math.floor(Math.random() * 2 + 1);
        var deltaX = Math.floor(Math.random() * 4);

        if(direction === 1){
            this.x += deltaX;
        }else{
            this.x -= deltaX;
        }

        this.y += this.speed;
    }
}
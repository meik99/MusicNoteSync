/**
 * Created by michael on 12/6/16.
 */
class Snow{
    constructor(width, height, size){
        this.size = size;
        this.width = width;
        this.height = height;
        this.deltaPos = 2;
    }

    initSnow(){
        this.snowflakes = new Array();
        for (let i = 0; i < this.size; i++){
            let startX = Math.floor(Math.random() * this.width);
            this.snowflakes.push(new Snowflake(startX));
        }
    }

    simulateNextPosition(){
        for(let i = 0; i < this.snowflakes.length; i++){
            if(this.snowflakes[i].y <= this.height) {
                this.snowflakes[i].calcNextPosition();
            }
        }

        for(let i = 0; i < this.size; i++){
            let newFlake = Math.floor(Math.random() * 100 + 1);
            if(newFlake <= 10){
                let newX = Math.floor(Math.random() * this.width);
                this.snowflakes.push(
                    new Snowflake(newX)
                );
            }
        }
    }

    drawSnow(context){

        context.beginPath();
        context.lineWidth = 2;
        context.strokeStyle = "white";

        for(let i = 0; i < this.snowflakes.length; i++){
            let snowflake = this.snowflakes[i];

            context.moveTo(snowflake.x - 1, snowflake.y - 1);
            context.lineTo(snowflake.x + 1, snowflake.y + 1);

            context.moveTo(snowflake.x + 1, snowflake.y - 1);
            context.lineTo(snowflake.x - 1, snowflake.y + 1);
        }

        context.stroke();
    }
}
package cn.nukkit.pathfinding;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.particle.LavaDripParticle;
import cn.nukkit.math.Vector3;

public class PathEntity{

    private final PathPoint[] points;
    private int currentPathIndex;
    private int pathLength;

    public PathEntity(PathPoint[] pathpoints){
        this.points = pathpoints;
        for(PathPoint point : pathpoints){

            Server.getInstance().getDefaultLevel().addParticle(new LavaDripParticle(new Vector3(point.xCoord, point.yCoord+5, point.zCoord)));
        }
        this.pathLength = pathpoints.length;
    }

    public void incrementPathIndex(){
        ++this.currentPathIndex;
    }

    public boolean isFinished(){
        return this.currentPathIndex >= this.pathLength;
    }

    public PathPoint getFinalPathPoint(){
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    public PathPoint getPathPointFromIndex(int index){
        return this.points[index];
    }

    public int getCurrentPathLength(){
        return this.pathLength;
    }

    public void setCurrentPathLength(int length){
        this.pathLength = length;
    }

    public int getCurrentPathIndex(){
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int currentPathIndexIn){
        this.currentPathIndex = currentPathIndexIn;
    }

    public Vector3 getVectorFromIndex(Entity entityIn, int index){//TODO 
        double d0 = (double)this.points[index].xCoord + (double)((int)(entityIn.getWidth() + 1.0F)) * 0.5D;
        double d1 = (double)this.points[index].yCoord;
        double d2 = (double)this.points[index].zCoord + (double)((int)(entityIn.getWidth() + 1.0F)) * 0.5D;
        return new Vector3(d0, d1, d2);
    }

    public Vector3 getPosition(Entity entityIn){
        return this.getVectorFromIndex(entityIn, this.currentPathIndex);
    }

    public boolean isSamePath(PathEntity pathentityIn){
        if (pathentityIn == null){
            return false;
        }else if (pathentityIn.points.length != this.points.length){
            return false;
        }else{
            for (int i = 0; i < this.points.length; ++i){
                if (this.points[i].xCoord != pathentityIn.points[i].xCoord || this.points[i].yCoord != pathentityIn.points[i].yCoord || this.points[i].zCoord != pathentityIn.points[i].zCoord){
                    return false;
                }
            }

            return true;
        }
    }

    public boolean isDestinationSame(Vector3 vec){
        PathPoint pathpoint = this.getFinalPathPoint();
        return pathpoint == null ? false : pathpoint.xCoord == (int)vec.x && pathpoint.zCoord == (int)vec.z;
    }
}
package cn.nukkit.level.pathfinder;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.MathHelper;
import cn.nukkit.pathfinding.PathPoint;
import cn.nukkit.utils.IntHashMap;

public abstract class NodeProcessor{

    protected Level blockaccess;
    protected IntHashMap<PathPoint> pointMap = new IntHashMap();
    protected int entitySizeX;
    protected int entitySizeY;
    protected int entitySizeZ;

    public void initProcessor(Level iblockaccessIn, Entity entityIn){
        this.blockaccess = iblockaccessIn;
        this.pointMap.clearMap();
        this.entitySizeX = MathHelper.floor_float(entityIn.getWidth() + 1.0F);
        this.entitySizeY = MathHelper.floor_float(entityIn.getHeight() + 1.0F);
        this.entitySizeZ = MathHelper.floor_float(entityIn.getWidth() + 1.0F);
    }

    public void postProcess(){
    }

    protected PathPoint openPoint(int x, int y, int z){
        int i = PathPoint.makeHash(x, y, z);
        PathPoint pathpoint = (PathPoint)this.pointMap.lookup(i);

        if (pathpoint == null){
            pathpoint = new PathPoint(x, y, z);
            this.pointMap.addKey(i, pathpoint);
        }

        return pathpoint;
    }

    public abstract PathPoint getPathPointTo(Entity entityIn);

    public abstract PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target);

    public abstract int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance);
}
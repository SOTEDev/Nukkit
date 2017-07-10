package cn.nukkit.pathfinding;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.pathfinder.NodeProcessor;
import cn.nukkit.math.BlockVector3;

public class PathFinder{

    private Path path = new Path();
    private PathPoint[] pathOptions = new PathPoint[32];
    private NodeProcessor nodeProcessor;

    public PathFinder(NodeProcessor nodeProcessorIn){
        this.nodeProcessor = nodeProcessorIn;
    }

    public PathEntity createEntityPathTo(Level blockaccess, Entity entityFrom, Entity entityTo, float dist){
        return this.createEntityPathTo(blockaccess, entityFrom, entityTo.x, entityTo.getBoundingBox().minY, entityTo.z, dist);
    }

    public PathEntity createEntityPathTo(Level blockaccess, Entity entityIn, BlockVector3 targetPos, float dist){
        return this.createEntityPathTo(blockaccess, entityIn, (double)((float)targetPos.getX() + 0.5F), (double)((float)targetPos.getY() + 0.5F), (double)((float)targetPos.getZ() + 0.5F), dist);
    }

    private PathEntity createEntityPathTo(Level blockaccess, Entity entityIn, double x, double y, double z, float distance){
        this.path.clearPath();
        this.nodeProcessor.initProcessor(blockaccess, entityIn);
        PathPoint pathpoint = this.nodeProcessor.getPathPointTo(entityIn);
        PathPoint pathpoint1 = this.nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
        PathEntity pathentity = this.addToPath(entityIn, pathpoint, pathpoint1, distance);
        this.nodeProcessor.postProcess();
        return pathentity;
    }

    private PathEntity addToPath(Entity entityIn, PathPoint pathpointStart, PathPoint pathpointEnd, float maxDistance){
        pathpointStart.totalPathDistance = 0.0F;
        pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
        pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(pathpointStart);
        PathPoint pathpoint = pathpointStart;

        while (!this.path.isPathEmpty()){
            PathPoint pathpoint1 = this.path.dequeue();

            if (pathpoint1.equals(pathpointEnd)){
                return this.createEntityPath(pathpointStart, pathpointEnd);
            }

            if (pathpoint1.distanceToSquared(pathpointEnd) < pathpoint.distanceToSquared(pathpointEnd)){
                pathpoint = pathpoint1;
            }

            pathpoint1.visited = true;
            int i = this.nodeProcessor.findPathOptions(this.pathOptions, entityIn, pathpoint1, pathpointEnd, maxDistance);

            for (int j = 0; j < i; ++j){
                PathPoint pathpoint2 = this.pathOptions[j];
                float f = pathpoint1.totalPathDistance + pathpoint1.distanceToSquared(pathpoint2);

                if (f < maxDistance * 2.0F && (!pathpoint2.isAssigned() || f < pathpoint2.totalPathDistance)){
                    pathpoint2.previous = pathpoint1;
                    pathpoint2.totalPathDistance = f;
                    pathpoint2.distanceToNext = pathpoint2.distanceToSquared(pathpointEnd);

                    if (pathpoint2.isAssigned()){
                        this.path.changeDistance(pathpoint2, pathpoint2.totalPathDistance + pathpoint2.distanceToNext);
                    }else{
                        pathpoint2.distanceToTarget = pathpoint2.totalPathDistance + pathpoint2.distanceToNext;
                        this.path.addPoint(pathpoint2);
                    }
                }
            }
        }

        if (pathpoint == pathpointStart){
            return null;
        }else{
            return this.createEntityPath(pathpointStart, pathpoint);
        }
    }

    private PathEntity createEntityPath(PathPoint start, PathPoint end){
        int i = 1;

        for (PathPoint pathpoint = end; pathpoint.previous != null; pathpoint = pathpoint.previous){
            ++i;
        }

        PathPoint[] apathpoint = new PathPoint[i];
        PathPoint pathpoint1 = end;
        --i;

        for (apathpoint[i] = end; pathpoint1.previous != null; apathpoint[i] = pathpoint1){
            pathpoint1 = pathpoint1.previous;
            --i;
        }

        return new PathEntity(apathpoint);
    }
}
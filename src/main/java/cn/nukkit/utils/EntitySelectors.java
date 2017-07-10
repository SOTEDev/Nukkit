package cn.nukkit.utils;

import com.google.common.base.Predicate;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;

public final class EntitySelectors{

    public static final Predicate<Entity> selectAnything = new Predicate<Entity>(){
        public boolean apply(Entity p_apply_1_){
            return p_apply_1_.isAlive();
        }
    };
    //public static final Predicate<Entity> IS_STANDALONE = new Predicate<Entity>(){
    //    public boolean apply(Entity p_apply_1_){
    //        return p_apply_1_.isAlive() && p_apply_1_.riddenByEntity == null && p_apply_1_.ridingEntity == null;
    //    }
    //};
    public static final Predicate<Entity> selectInventories = new Predicate<Entity>(){
        public boolean apply(Entity p_apply_1_){
            return p_apply_1_ instanceof Inventory && p_apply_1_.isAlive();
        }
    };
    public static final Predicate<Entity> NOT_SPECTATING = new Predicate<Entity>(){
        public boolean apply(Entity p_apply_1_){
            return !(p_apply_1_ instanceof Player) || !((Player)p_apply_1_).isSpectator();
        }
    };
}
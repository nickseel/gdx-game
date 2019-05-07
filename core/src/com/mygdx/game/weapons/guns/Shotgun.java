package com.mygdx.game.weapons.guns;

import com.mygdx.game.entities.projectiles.TestProjectile;
import com.mygdx.game.textures.TextureData;
import com.mygdx.game.weapons.Weapon;
import com.mygdx.game.weapons.WeaponController;
import com.mygdx.game.weapons.WeaponGenerator;
import com.mygdx.game.weapons.WeaponType;
import com.mygdx.game.weapons.stats.WeaponStat;

public class Shotgun extends Weapon {
    public static WeaponGenerator generator = new WeaponGenerator() {
        @Override
        public Weapon generateWeapon() {
            return new Shotgun();
        }
    };

    private static final WeaponStat[] availableStats = new WeaponStat[] {
            WeaponStat.BULLET_DAMAGE,
            WeaponStat.BULLET_NUM,
            WeaponStat.BULLET_SPEED,
            WeaponStat.BULLET_SIZE,
            WeaponStat.BULLET_KNOCKBACK,
            WeaponStat.WEAPON_SPREAD,
            WeaponStat.WEAPON_FIRE_RATE,
            WeaponStat.WEAPON_KICK,
    };
    private static final double[][] defaultStats = new double[][] {
            {7.0, 1.5},      //DAMAGE
            {4.5, 1.0},      //BULLET NUM
            {500, 75},       //BULLET SPEED
            {10.0, 1.2},     //BULLET SIZE
            {100, 20},       //BULLET KNOCKBACK
            {0.08, 0.02},    //WEAPON SPREAD
            {1.20, 0.3},     //WEAPON FIRE RATE
            {375.0, 75.0},   //WEAPON KICK
    };
    private static final double BULLET_SPEED_VARIATION = 0.1;

    private double fireTimer = 0;

    public Shotgun() {
        super(WeaponType.SHOTGUN, TextureData.WEAPONS, 2, availableStats, defaultStats);

        randomizeVariationRolls();
        initStats();
        updateWeaponInfo();
    }

    @Override
    public void update(double delta, WeaponController controller) {
        boolean firing = controller.isFiringWeapon();
        //boolean prevFiring = controller.wasFiringWeapon();

        //System.out.println(fireTimer);
        double fireTime = 1.0 / stats[6][1];
        if(firing) {
            fireTimer += delta;

            while(fireTimer >= fireTime) {
                fireTimer -= fireTime;

                fire(delta, controller);
            }
        } else {
            fireTimer = Math.min(fireTime, fireTimer + delta);
        }
    }

    protected void fire(double delta, WeaponController controller) {
        double angle = Math.atan2(controller.getTargetY(), controller.getTargetX());
        double maxAngleVariation = Math.toRadians(180) * stats[5][1];

        int numBullets = (int)(stats[1][1] + Math.random());
        double individualAngleVariation = maxAngleVariation * (1.0 / numBullets+1);

        controller.kick(angle + Math.PI, stats[7][1]);


        for(int i = 0; i < numBullets; i++) {
            double variedAngle = angle - maxAngleVariation + individualAngleVariation*i * Math.random() * individualAngleVariation;

            new TestProjectile(controller, this, TextureData.PLAYER_SHEET,
                    controller.getX(), controller.getY(),
                    stats[0][1],   //damage
                    stats[2][1] * ((((Math.random() * 2) - 1) * BULLET_SPEED_VARIATION) + 1),   //speed
                    variedAngle,   //angle
                    stats[3][1],   //bullet radius
                    stats[4][1]);  //knockback
        }
    }
}
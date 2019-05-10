package com.mygdx.game.ui.pause_menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.textures.TextureData;
import com.mygdx.game.textures.TextureManager;
import com.mygdx.game.ui.FontManager;
import com.mygdx.game.ui.elements.*;
import com.mygdx.game.ui.graphic_types.BlankGT;
import com.mygdx.game.ui.graphic_types.RectBorderGT;
import com.mygdx.game.ui.graphic_types.RectGT;
import com.mygdx.game.ui.graphic_types.RectTrueBorderGT;
import com.mygdx.game.views.PauseMenuView;
import com.mygdx.game.weapons.Weapon;
import com.mygdx.game.weapons.stats.WeaponRarity;

import static com.mygdx.game.util.Util.makeGray;

public class WeaponIconUI extends FloaterUI {
    protected final PauseMenuView pauseMenuView;
    protected WeaponIconContainer container;
    protected Weapon weapon = null;

    protected float width, height;

    protected SwapperUI emptyIconSwapper;
    protected UI weaponIcon, weaponIconSpriteContainer, weaponIconStarContainer;
    protected TextUI weaponIconText;
    protected ButtonUI weaponIconButton;
    protected final float iconBorderSize = 3.0f;
    protected final float shadowOffset = 4.0f;

    public WeaponIconUI(PauseMenuView pauseMenu, WeaponIconContainer container, float width, float height, float textHeight) {
        this.pauseMenuView = pauseMenu;
        this.container = container;
        this.width = width;
        this.height = height;
        setSize(width, height);
        setContentFill(true, true);

        emptyIconSwapper = new SwapperUI();
        emptyIconSwapper.addToParent(this);


        SwapperUI emptyIconFrame = new SwapperUI();
        UI emptyIconBorder = new UI()
                .setGraphicType(new RectTrueBorderGT().setColor(Color.CLEAR).setBorder(new float[] {3.0f}, new Color[] {makeGray(1.0f, 0.85f)}));
        UI emptyIconTexture = new UI()
                .setGraphicType(new RectGT(TextureManager.getTexture(TextureData.DIAGONALS)).setColor(makeGray(0.0f, 0.6f)));
        emptyIconFrame.addChild("texture", emptyIconTexture).addChild("border", emptyIconBorder).setAllVisible(true);


        SwapperUI weaponIconFrame = new SwapperUI();
        UI weaponIconShadow = new UI() {
            @Override
            public void draw(SpriteBatch batch) {
                graphicType.draw(batch, centerX + shadowOffset, centerY - shadowOffset, width, height);
                drawChildren(batch);
            }
        }.setGraphicType(new RectGT().setColor(PauseMenuView.SHADOW_COLOR));


        if(pauseMenuView != null) {
            final WeaponIconUI icon = this;
            weaponIconButton = new ButtonUI(makeGray(0, 0), makeGray(0, 0.1f), makeGray(0, 0.2f), new RectGT()) {
                public void press(double hoverTimer, double pressTimer)     { pauseMenuView.pressIcon(icon, hoverTimer, pressTimer); }
                public void hold(double hoverTimer, double pressTimer)      { pauseMenuView.holdIcon(icon, hoverTimer, pressTimer); }
                public void release(double hoverTimer, double pressTimer)   { pauseMenuView.releaseIcon(icon, hoverTimer, pressTimer); }
                public void mouseOver(double hoverTimer)                    { pauseMenuView.mouseOverIcon(icon, hoverTimer); }
                public void hover(double hoverTimer)                        { pauseMenuView.hoverIcon(icon, hoverTimer); }
                public void mouseLeave(double hoverTimer)                   { pauseMenuView.mouseLeaveIcon(icon, hoverTimer); }
            };//.setContentFill(true, true);//.addToParent(weaponIcon);
        }

        weaponIcon = new UI().setContentFill(true, true)
                .setGraphicType(new RectGT().setColor(new Color(1, 0, 0, 1)));
        UI weaponIconTextContainer = new UI().setHeight(textHeight).setContentFill(true, true).addToParent(weaponIcon);
        weaponIconText = new TextUI("TEST HELLO", FontManager.aireExterior18, Color.WHITE);
        weaponIconText.setTextAlign(Align.center, Align.center).setTruncate("").setOffset(0, -2).addToParent(weaponIconTextContainer);

        SwapperUI weaponIconSpriteStarOverlay = new SwapperUI();
        weaponIconSpriteStarOverlay.addToParent(weaponIcon);

        weaponIconSpriteContainer = new UI().setMargin(iconBorderSize, iconBorderSize).setContentFill(true, true)
                .setGraphicType(new RectBorderGT().setColor(makeGray(0.85f, 1.0f)));
        weaponIconStarContainer = new UI().setContentAlign(UI.BOTTOM, UI.CENTER).setPadding(0, 4);

        weaponIconSpriteStarOverlay.addChild("sprite", weaponIconSpriteContainer).addChild("stars", weaponIconStarContainer)
                .setAllVisible(true);


        weaponIconFrame.addChild("shadow", weaponIconShadow).addChild("icon", weaponIcon)
                .setAllVisible(true);


        emptyIconSwapper.addChild("empty", emptyIconFrame).addChild("icon", weaponIconFrame);
        if(pauseMenuView != null)
            emptyIconSwapper.addChild("button", weaponIconButton);
        setWeapon(null);

        format();
    }

    public WeaponIconUI setWeapon(Weapon weapon) {
        this.weapon = weapon;

        if(weapon == null) {
            emptyIconSwapper.setAllVisible(false).setViewVisible("empty", true).setViewVisible("button", true);

            if(pauseMenuView != null) {
                weaponIconButton.setGraphicType(new RectGT() {
                    public void draw(SpriteBatch batch, float centerX, float centerY, float width, float height) {
                    }
                });
            }

            format();
        } else {
            emptyIconSwapper.setAllVisible(false).setViewVisible("icon", true).setViewVisible("button", true);

            if(pauseMenuView != null)
                weaponIconButton.setGraphicType(new RectGT());

            WeaponRarity rarity = weapon.getRarity();
            weaponIconText.setColor(rarity.getTextColor());
            weaponIconText.setText(weapon.getName());
            weaponIcon.getGraphicType().setColor(rarity.getMainColor());
            weaponIconSpriteContainer.getGraphicType().setBorder(new float[] {iconBorderSize}, new Color[] {rarity.getBorderColor()});

            weaponIconSpriteContainer.removeChildren();
            weaponIconSpriteContainer.addChild(new UI().setMargin(4, 4).setGraphicType(new RectGT(weapon.getTexture()).setColor(makeGray(0.3f, 1f))));
            weaponIconSpriteContainer.format();

            weaponIconStarContainer.removeChildren();
            weaponIconStarContainer.addChild(new TextUI("***", FontManager.aireExterior48, rarity.getMainColor()).setShadow(rarity.getTextColor(), 1, -1));
            weaponIconStarContainer.format();

            format();
        }
        return this;
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        //updateChildren(delta);
        //System.out.println(centerX + " " + centerY + " " + width + " " + height);

    }

    @Override
    public void draw(SpriteBatch batch) {
        //graphicType.setColor(currentColor).draw(batch, centerX, centerY, width, height);
    }

    public void drawReal(SpriteBatch batch) {
        drawChildren(batch);
    }

    public void moveToFront() {
        container.moveToFront(this);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public WeaponIconContainer getContainer() {
        return container;
    }
}
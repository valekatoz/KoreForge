package net.kore.ui.hud;

public class DraggableComponent extends Component
{
    public double startX;
    public double startY;
    private boolean dragging;

    public boolean isDragging() {
        return this.dragging;
    }

    public void startDragging() {
        this.dragging = true;
        this.startX = this.x - getMouseX();
        this.startY = this.y - getMouseY();
    }

    public void stopDragging() {
        this.dragging = false;
    }

    public void onTick() {
    }

    public HudVec drawScreen() {
        if (this.dragging) {
            this.y = getMouseY() + this.startY;
            this.x = getMouseX() + this.startX;
        }
        return null;
    }
}


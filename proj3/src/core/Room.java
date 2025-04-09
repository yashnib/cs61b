package core;

public class Room {

    private final int MAX_ROOM_DIMENSION = 8;
    private final int MIN_ROOM_DIMENSION = 5;

    private int width;
    private int height;
    private final Point position;
    private static int id = 0;
    private final int roomID;

    private boolean isHorizontallySplit;
    private boolean isVerticallySplit;

    private boolean hasHorizontalHallway;
    private boolean hasVerticalHallway;

    public Room(int width, int height, Point position, boolean isHorizontallySplit, boolean isVerticallySplit) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be a positive integer");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Height must be a positive integer");
        }
        this.width = width;
        this.height = height;
        this.position = position;
        this.isHorizontallySplit = isHorizontallySplit;
        this.isVerticallySplit = isVerticallySplit;
        hasHorizontalHallway = false;
        hasVerticalHallway = false;
        roomID = Room.id;
        Room.id += 1;
    }

    public int getWidth() {
        return width;
    };

    public int getHeight() {
        return height;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public boolean getHorizontalHallway() {
        return hasHorizontalHallway;
    }

    public boolean getVerticalHallway() {
        return hasVerticalHallway;
    }

    public void setHorizontalHallway() {
        hasHorizontalHallway = true;
    }

    public void setVerticalHallway() {
        hasVerticalHallway = true;
    }

    public void setHorizontalSplit() {
        isHorizontallySplit = true;
    }

    public void setVerticalSplit() {
        isVerticallySplit = true;
    }

    public boolean getHorizontalSplit() {
        return isHorizontallySplit;
    }

    public boolean getVerticalSplit() {
        return isVerticallySplit;
    }

    public void makeIntersection() {
        this.width = 1;
        this.height = 1;
    }

    public void setHeight() {
    }
    public int getMinRoomDimension() {
        return MIN_ROOM_DIMENSION;
    }

    public int getID() {
        return roomID;
    }
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Room other = (Room) obj;
        return getX() == other.getX() && getY() == other.getY() && width == other.getWidth() && height == other.getHeight();
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + Integer.hashCode(width);
        result = 31 * result + Integer.hashCode(height);
        result = 31 * result + position.hashCode();
        return result;
    }
}

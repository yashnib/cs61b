package core;

public class Room {

    private final int MAX_ROOM_DIMENSION = 8;
    private final int MIN_ROOM_DIMENSION = 5;

    private int width;
    private int height;
    private final Point position;

    private boolean isHorizontallySplit;
    private boolean isVerticallySplit;

    private final boolean drawOpposite;

    public Room(int width, int height, Point position, boolean drawOpposite) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be a positive integer");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Height must be a positive integer");
        }
        this.width = width;
        this.height = height;
        this.position = position;
        this.drawOpposite = drawOpposite;
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

    public boolean getDrawOpposite() {
        return drawOpposite;
    }

    public int getMinRoomDimension() {
        return MIN_ROOM_DIMENSION;
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

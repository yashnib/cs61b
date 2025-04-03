package core;

public class Room {

    private final int MAX_ROOM_DIMENSION = 8;
    private final int MIN_ROOM_DIMENSION = 3;

    private final int width;
    private final int height;
    private final Point position;

    public Room(int width, int height, Point position) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must be positive integers.");
        }

        this.width = width;
        this.height = height;
        this.position = position;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getMaxDimension() {
        return MAX_ROOM_DIMENSION;
    }

    public int getMinDimension() {
        return MIN_ROOM_DIMENSION;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Room other = (Room) obj;
        return position.getX() == other.getX() && position.getY() == other.getY() && width == other.getWidth() && height == other.getHeight();
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + Integer.hashCode(width);
        result = 31 * result + Integer.hashCode(height);
        result = 31 * result + position.hashCode();
        return result;
    }
}

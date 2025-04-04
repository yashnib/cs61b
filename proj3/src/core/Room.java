package core;

public class Room {

    private final int MAX_ROOM_DIMENSION = 8;
    private final int MIN_ROOM_DIMENSION = 3;

    private final int left;
    private final int right;
    private final int top;
    private final int bottom;

    public Room(int left, int right, int top, int bottom) {
        if (left < 0) {
            throw new IllegalArgumentException("Left must be a non-negative integer");
        }
        if (right < 0) {
            throw new IllegalArgumentException("Right must be a non-negative integer");
        }
        if (top < 0) {
            throw new IllegalArgumentException("Top must be a non-negative integer");
        }
        if (bottom < 0) {
            throw new IllegalArgumentException("Bottom must be a non-negative integer");
        }
        if (left >= right) {
            throw new IllegalArgumentException("Left must be smaller than right");
        }
        if (bottom >= top) {
            throw new IllegalArgumentException("Bottom must be smaller than top");
        }

        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public int getWidth() {
        return right - left + 1;
    };

    public int getHeight() {
        return top - bottom + 1;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Room other = (Room) obj;
        return left == other.getLeft() && right == other.getRight() && top == other.getTop() && bottom == other.getBottom();
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + Integer.hashCode(left);
        result = 31 * result + Integer.hashCode(right);
        result = 31 * result + Integer.hashCode(top);
        result = 31 * result + Integer.hashCode(bottom);
        return result;
    }
}

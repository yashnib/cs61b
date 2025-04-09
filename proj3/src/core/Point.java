package core;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        if (x < 0) {
            throw new IllegalArgumentException("The x-coordinate must be a non-negative integer");
        }
        if (y < 0) {
            throw new IllegalArgumentException("The y-coordinate must be non-negative integer");
        }

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Point other = (Point) obj;
        return other.getX() == x && other.getY() == y;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}

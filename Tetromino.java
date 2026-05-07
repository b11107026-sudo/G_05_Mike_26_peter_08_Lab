import java.awt.*;
import java.util.Random;

public enum Tetromino {
    I(new int[][][]{
            {
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
            },
            {
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0}
            }
    }, new Color(0, 240, 240)),
    J(new int[][][]{
            {
                    {1, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 1},
                    {0, 1, 0},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 0, 1}
            },
            {
                    {0, 1, 0},
                    {0, 1, 0},
                    {1, 1, 0}
            }
    }, new Color(0, 0, 240)),
    L(new int[][][]{
            {
                    {0, 0, 1},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 0},
                    {0, 1, 1}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {1, 0, 0}
            },
            {
                    {1, 1, 0},
                    {0, 1, 0},
                    {0, 1, 0}
            }
    }, new Color(240, 160, 0)),
    O(new int[][][]{
            {
                    {1, 1},
                    {1, 1}
            }
    }, new Color(240, 240, 0)),
    S(new int[][][]{
            {
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 0, 1}
            }
    }, new Color(0, 240, 0)),
    T(new int[][][]{
            {
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 1, 0},
                    {1, 1, 0},
                    {0, 1, 0}
            }
    }, new Color(160, 0, 240)),
    Z(new int[][][]{
            {
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 0, 1},
                    {0, 1, 1},
                    {0, 1, 0}
            }
    }, new Color(240, 0, 0));

    private final int[][][] shapes;
    private final Color color;
    private static final Tetromino[] VALUES = values();
    private static final Random RANDOM = new Random();

    Tetromino(int[][][] shapes, Color color) {
        this.shapes = shapes;
        this.color = color;
    }

    public int[][] getShape(int rotation) {
        return shapes[rotation % shapes.length];
    }

    public int getRotationCount() {
        return shapes.length;
    }

    public Color getColor() {
        return color;
    }

    public static Tetromino random() {
        return VALUES[RANDOM.nextInt(VALUES.length)];
    }
}

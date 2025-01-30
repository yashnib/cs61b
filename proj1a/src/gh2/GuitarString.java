package gh2;
import deque.*;

//Note: This file will not compile until you complete the Deque61B implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque61B<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        buffer = new LinkedListDeque61B<>();
        int roundedFrequency = (int) Math.round(frequency);
        int capacity = SR/roundedFrequency;
        for (int i = 0; i < capacity; i++){
            buffer.addFirst(0.0);
        }
    }

    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        int bufSize = buffer.size();
        for (int i = 0; i < bufSize; i++){
            buffer.removeFirst();
            double r = Math.random() - 0.5;
            buffer.addLast(r);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        int bufSize = buffer.size();
        double bufFirst = buffer.removeFirst();
        double bufSecond = buffer.get(0);
        double average = DECAY*(bufFirst + bufSecond)/2;
        buffer.addLast(average);
        }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}

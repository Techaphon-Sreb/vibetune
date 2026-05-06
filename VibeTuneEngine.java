import java.util.*;

class Song {
    String title;
    Song next;
    Song prev;

    Song(String title) {
        this.title = title;
    }
}

public class VibeTuneEngine {
    private Song head = null;
    private Song current = null;
    
    // 1. Stack สำหรับ Recently Played (History) - LIFO
    private Stack<String> history = new Stack<>();
    
    // 2. Queue สำหรับ Up Next (Booking) - FIFO
    private Queue<String> upNext = new LinkedList<>();
    private List<String> queueBackup = new ArrayList<>();
    private boolean loopMode = false;

    // 3. DLL & CLL สำหรับ Main Playlist
    public void addSong(String title) {
        Song newSong = new Song(title);
        if (head == null) {
            head = newSong;
            head.next = head; //CLL
            head.prev = head;
            current = head;
        } else {
            Song tail = head.prev;
            tail.next = newSong;
            newSong.prev = tail;
            newSong.next = head;
            head.prev = newSong;
        }
        System.out.println(">> Added to Library: " + title);
    }

    public void playNext() {
        if (current == null) {
            System.out.println(">> No songs in playlist!");
            return;
        }
        history.push(current.title); //Stack
        current = current.next;
        System.out.println(">> Now Playing: " + current.title);
    }

    //Recursion Deep Scan
    /*public void scanMusicFolders(int level) {
        if (level == 0) return;
        System.out.println("...Scanning Sub-folder Level " + level + " [Recursive Search]");
        scanMusicFolders(level - 1);
    }*/

    public static void main(String[] args) {
        VibeTuneEngine vibe = new VibeTuneEngine();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== VibeTune Java Engine v1.0 ===");
        //vibe.scanMusicFolders(3);

        boolean running = true;
        while (running) { // Iterative Loop
            System.out.println("\n[1] Add Song [2] Next Song [3] Add to Queue [4] Show History [5] Exit [6] Toggle Loop");
            if (vibe.current != null) {
                System.out.println("\n>> Current Song: " + vibe.current.title);
            } else {
                System.out.println("\n>> Current Song: None");
            }
            System.out.println(">> Loop Mode: " + (vibe.loopMode ? "ON" : "OFF"));
            System.out.print("Action: ");
            String choiceInput = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid input! Please enter a number from 1 to 5.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Song Name: ");
                    vibe.addSong(sc.nextLine());
                    break;
                case 2:
                    if (vibe.upNext.isEmpty() && vibe.loopMode && !vibe.queueBackup.isEmpty()) {
                        vibe.upNext.addAll(vibe.queueBackup);
                        System.out.println(">> Queue loop is ON. Repeating queued songs.");
                    }

                    if (!vibe.upNext.isEmpty()) {
                        String queuedSong = vibe.upNext.poll();
                        System.out.println(">> Playing from Queue: " + queuedSong);
                        vibe.history.push(queuedSong);
                        System.out.println(">> Now Playing: " + queuedSong);
                    } else {
                        if (!vibe.loopMode) {
                            System.out.println(">> No more songs in queue.");
                        }
                        vibe.playNext();
                    }
                    break;
                case 3:
                    System.out.print("Enter Song to Queue: ");
                    String qSong = sc.nextLine();
                    vibe.upNext.add(qSong);
                    vibe.queueBackup.add(qSong);
                    System.out.println(">> Enqueued: " + qSong);
                    break;
                case 4:
                    if (!vibe.history.isEmpty()) {
                        System.out.println(">> Last Played: " + vibe.history.peek() + " (Pop to see more)");
                        vibe.history.pop();
                    } else {
                        System.out.println(">> History is empty!");
                    }
                    break;
                case 5:
                    running = false;
                    System.out.println("Vibe out! See ya.");
                    break;
                case 6:
                    vibe.loopMode = !vibe.loopMode;
                    System.out.println(">> Loop mode is now " + (vibe.loopMode ? "ON" : "OFF") + ".");
                    break;
                default:
                    System.out.println("Invalid choice, bro.");
            }
        }
        sc.close();
    }
}
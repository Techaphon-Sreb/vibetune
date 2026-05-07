import java.util.*;

// --- DATA STRUCTURES ---

class Song {
    String title;
    int duration;
    Song next;
    Song prev;

    Song(String title, int duration) {
        this.title = title;
        this.duration = duration;
    }
}

public class VibeTuneEngine {
    private Song head = null;
    private Song current = null;

    // Stack
    private Stack<String> history = new Stack<>();

    // Queue
    private Queue<String> logs = new LinkedList<>();

    private boolean loopMode = false;

    // DLL & CLL
    public void addSong(String title, int duration) {
        Song newSong = new Song(title, duration);
        if (head == null) {
            head = newSong;
            head.next = head; // CLL: ตัวสุดท้ายชี้กลับตัวแรก
            head.prev = head;
            current = head;
        } else {
            Song tail = head.prev;
            tail.next = newSong;
            newSong.prev = tail;
            newSong.next = head;
            head.prev = newSong;
        }
        addLog("Added: " + title);
        System.out.println(">> Added: " + title + " (" + (duration / 60) + "m " + (duration % 60) + "s)");
    }

    public void playNext() {
        if (current == null) {
            System.out.println(">> No songs in playlist!");
            return;
        }

        // Logic สำหรับ Toggle Loop: ถ้าถึงเพลงสุดท้ายแล้ว Loop OFF จะไม่วนกลับ
        if (current.next == head && !loopMode) {
            System.out.println(">> End of playlist. Enable Loop Mode to repeat.");
            addLog("Reached end of list (Loop OFF)");
            return;
        }

        history.push(current.title); // ใช้ Stack เก็บประวัติ
        current = current.next;
        addLog("Skipped to: " + current.title);
        System.out.println(">> Now Playing: " + current.title);
    }

    // ITERATIVE
    public void displayLibrary() {
        if (head == null) {
            System.out.println(">> Library is empty!");
            return;
        }
        System.out.println("\n--- Music Library (Iterative Display) ---");
        Song temp = head;
        do {
            String marker = (temp == current) ? "[PLAYING] -> " : "           - ";
            System.out.println(marker + temp.title + " (" + (temp.duration / 60) + "m " + (temp.duration % 60) + "s)");
            temp = temp.next;
        } while (temp != head); // เงื่อนไขหยุดเมื่อวนครบรอบ CLL
    }

    // RECURSION
    public int calculateTotalDuration(Song node, Song startNode, boolean isFirst) {
        if (node == null || (!isFirst && node == startNode)) {
            return 0;
        }
        return node.duration + calculateTotalDuration(node.next, startNode, false);
    }

    // Queue
    private void addLog(String message) {
        if (logs.size() >= 3) {
            logs.poll(); // เอาตัวเก่าสุดออกเมื่อเกินโควตา
        }
        logs.add(message);
    }

    public static void main(String[] args) {
        VibeTuneEngine vibe = new VibeTuneEngine();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== VibeTune ===");

        boolean running = true;
        while (running) { // Iterative Loop
            System.out.println("\n[1] Add Song [2] Next Song [3] View Library [4] History ");
            System.out.println(
                    "[5] Total Duration [6] Toggle Loop [7] System Logs) [8] Exit");

            if (vibe.current != null) {
                System.out.println("\n>> Currently At: " + vibe.current.title + " (" + (vibe.current.duration / 60) + "m " + (vibe.current.duration % 60) + "s)");
            }
            System.out.println(">> Loop Mode: " + (vibe.loopMode ? "ON" : "OFF"));
            System.out.print("Action: ");

            String choiceInput = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid input! Please enter 1-8.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Song Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Duration (e.g., 3.15 for 3m 15s): ");
                    try {
                        double val = Double.parseDouble(sc.nextLine());
                        int mins = (int) val;
                        int secs = (int) Math.round((val - mins) * 100);
                        int dur = mins * 60 + secs;
                        vibe.addSong(name, dur);
                    } catch (Exception e) {
                        System.out.println(">> Error: Please input only numbers for duration.");
                    }
                    break;
                case 2:
                    vibe.playNext();
                    break;
                case 3:
                    vibe.displayLibrary();
                    break;
                case 4:
                    if (!vibe.history.isEmpty()) {
                        System.out.println(">> Last Played: " + vibe.history.pop());
                    } else {
                        System.out.println(">> History is empty!");
                    }
                    break;
                case 5:
                    if (vibe.head != null) {
                        int total = vibe.calculateTotalDuration(vibe.head, vibe.head, true);
                        System.out.println(
                                ">> Playlist Duration: " + (total / 60) + "m " + (total % 60) + "s (Recursive)");
                    } else {
                        System.out.println(">> Playlist is empty!");
                    }
                    break;
                case 6:
                    vibe.loopMode = !vibe.loopMode;
                    vibe.addLog("Toggle Loop: " + (vibe.loopMode ? "ON" : "OFF"));
                    System.out.println(">> Loop mode is now " + (vibe.loopMode ? "ON" : "OFF") + ".");
                    break;
                case 7:
                    System.out.println("--- System Logs (Queue FIFO) ---");
                    if (vibe.logs.isEmpty())
                        System.out.println("No activity logs.");
                    for (String s : vibe.logs)
                        System.out.println(" - " + s);
                    break;
                case 8:
                    running = false;
                    System.out.println("Vibe out! See ya.");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        sc.close();
    }
}
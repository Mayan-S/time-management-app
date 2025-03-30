import java.util.Scanner;
import java.io.*;
import java.util.InputMismatchException;
import java.time.LocalDate;

class Main {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in); // Scanner for user input
    LocalDate currentDate = LocalDate.now(); // Get the current date
    int option, taskNum = 1, date, number = 0; // Variables for menu option, task number, due date, etc.

    // Main menu loop
    do {
      // Input validation loop for menu option
      do {
        System.out.println("Time Management App");
        System.out.println("1. Add Task");
        System.out.println("2. List Tasks");
        System.out.println("3. Mark Task as Completed");
        System.out.println("4. Quit");

        try {
          System.out.print("Select an option: ");
          option = input.nextInt(); // Read user input

          // Check if input is within valid range
          if (option < 1 || option > 4) {
            System.out.println("Invalid Input!\n");
          }
        } catch (InputMismatchException err) {
          // Handle non-integer input
          System.out.println("Invalid Input!\n");
          option = -1;
          input.next(); // Clear invalid input
        }
      } while (option < 1 || option > 4); // Repeat until valid input

      // Handle menu options
      switch (option) {
        case 1: // Add Task
          System.out.print("\nEnter Task Description for #" + taskNum + " (add '_' for spaces): ");
          String task = input.next(); // Read task description

          // Input validation loop for due date
          do {
            try {
              System.out.print("In How Many Days Is The Task Due(#>0): ");
              date = input.nextInt(); // Read due date
            } catch (InputMismatchException err) {
              date = -1;
              input.next(); // Clear invalid input
            }
          } while (date <= 0); // Repeat until valid date

          // Write task to file
          try {
            FileWriter fw = new FileWriter("tasks.txt", true); // Append mode
            PrintWriter pw = new PrintWriter(fw);
            pw.println(taskNum + "."); // Task number
            pw.println(task);         // Task description
            pw.println(date);         // Due in how many days
            pw.close();
          } catch (IOException err) {
            System.out.println("Error");
          }

          System.out.println("\nTask Added!");
          taskNum++; // Increment task number
          break;

        case 2: // List Tasks
          System.out.print("\n");
          try {
            FileReader fr = new FileReader("tasks.txt");
            BufferedReader br = new BufferedReader(fr);
            boolean proceed, empty = false;

            // Read tasks in sets of 3 lines (task number, description, due days)
            String taskNumOne = br.readLine();
            String taskOne = br.readLine();
            String dateOne = br.readLine();

            while (taskNumOne != null) {
              proceed = false;

              // Check if task is marked as completed
              FileReader fr2 = new FileReader("remove.txt");
              BufferedReader br2 = new BufferedReader(fr2);
              String removeLine;

              do {
                removeLine = br2.readLine();
                if (taskNumOne.equalsIgnoreCase(removeLine)) {
                  proceed = true; // Task is completed, skip it
                }
              } while (removeLine != null);

              if (!proceed) {
                empty = true; // At least one task found
                try {
                  number = Integer.parseInt(dateOne); // Convert due days to int
                } catch (NumberFormatException err) {}

                LocalDate newDate = currentDate.plusDays(number); // Calculate due date
                System.out.println("\nTask " + taskNumOne + " " + taskOne);
                System.out.println("Due in " + dateOne + " days (" + newDate + ")\n");
              }

              // Read next task
              taskNumOne = br.readLine();
              taskOne = br.readLine();
              dateOne = br.readLine();
              br2.close();
            }

            br.close();

            if (!empty) {
              System.out.println("No Tasks Found!");
            }
          } catch (IOException err) {
            System.out.println("Error");
          }

          System.out.print("\nEnter a key to exit: ");
          String exit = input.next(); // Pause before clearing screen
          break;

        case 3: // Mark Task as Completed
          int taskNumTwo;
          boolean escape;

          // Input validation loop for task number
          do {
            try {
              System.out.print("Enter the task number to mark as completed (#>0): ");
              taskNumTwo = input.nextInt();
            } catch (InputMismatchException err) {
              taskNumTwo = -1;
              input.next(); // Clear invalid input
            }
          } while (taskNumTwo <= 0);

          try {
            FileReader fr = new FileReader("tasks.txt");
            BufferedReader br = new BufferedReader(fr);
            FileReader fr2 = new FileReader("remove.txt");
            BufferedReader br2 = new BufferedReader(fr2);
            FileWriter fw = new FileWriter("remove.txt", true); // Append to remove.txt
            PrintWriter pw = new PrintWriter(fw);

            String skipLine, readLine;
            boolean finish = false;

            // Check if task is already marked as completed
            readLine = br2.readLine();
            while (readLine != null) {
              if (readLine.equalsIgnoreCase(taskNumTwo + ".")) {
                System.out.println("You Already Inputed This Value!");
                finish = true;
              }
              readLine = br2.readLine();
            }

            // If not already completed, find and mark it
            if (!finish) {
              do {
                skipLine = br.readLine();
                if (skipLine == null) {
                  System.out.println("Could Not Find Task!");
                  escape = true;
                } else if (skipLine.equalsIgnoreCase(taskNumTwo + ".")) {
                  pw.println(taskNumTwo + "."); // Mark as completed
                  System.out.println("Task Marked as Completed!");
                  escape = true;
                } else {
                  escape = false;
                }
              } while (!escape);
            }

            br.close();
            pw.close();
          } catch (IOException err) {
            System.out.println("Error");
          }
          break;
      }

      // Simulate loading animation for options 1, 2, or 3
      if (option == 3 || option == 2 || option == 1) {
        System.out.print("Please Wait");
        for (int x = 0; x < 3; x++) {
          System.out.print(".");
          try {
            Thread.sleep(1000); // Wait 1 second
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

      // Clear console
      System.out.print("\033[H\033[2J");
      System.out.flush();

    } while (option != 4); // Exit loop when user selects option 4

    // Clear both files on exit
    try {
      FileWriter fw = new FileWriter("remove.txt");
      PrintWriter pw = new PrintWriter(fw);
      FileWriter fw2 = new FileWriter("tasks.txt");
      PrintWriter pw2 = new PrintWriter(fw2);
      pw.close();
      pw2.close();
    } catch (IOException err) {
      System.out.println("IDK");
    }
  }
}

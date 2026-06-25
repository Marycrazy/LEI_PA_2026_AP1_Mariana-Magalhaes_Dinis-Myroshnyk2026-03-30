package main.states.OLD;
// package main.states;

// import main.DatabaseManager;
// import main.utils.Input;
// import main.utils.PressKey;

// public class EmployeeMenuState extends State {
//     @Override
//     public void render() {
//         long notifications = DatabaseManager.getInstance().getUnreadNotifications(user);

//         System.out.println("--- EMPLOYEE DASHBOARD [user: " + user.getUsername() + "] ---");
//         System.out.println("Notifications [" + notifications + " pending]\n");
//         System.out.println("--- EMPLOYEE MENU ---");
//         System.out.println("1. View repairs");
//         System.out.println("2. View notifications");
//         System.out.println("3. Change profile");
//         System.out.println("0. Sign out");
//         System.out.print("Choice: ");
//     }

//     @Override
//     public void handleInput() {
//         String input = Input.getScanner().nextLine();

//         switch (input) {
//             case "1": new ListRepairsState().enter();break;
//             case "2": new NotificationMenuState().enter(); break;
//             case "3": new EditUserState(user).enter(); break;
//             case "0": this.back(); this.back(); break;
//             default:
//                 System.out.println("Invalid option!");
//                 PressKey.enter();
//                 break;
//         }
//     }
// }
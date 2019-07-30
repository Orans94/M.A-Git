public class Menu
{
    public static void requestUserName()
    {
        System.out.println("Please enter user name");
    }

    public void Show()
    {
        System.out.println("Please choose a number from the list below:");
        System.out.println("1. Update user name");
        System.out.println("2. Initialize repository");
        System.out.println("3. Read repository details from XML file");
        System.out.println("4. Change repository");
        System.out.println("5. Show details of current commit");
        System.out.println("6. Show status");
        System.out.println("7. Commit");
        System.out.println("8. Show all branches");
        System.out.println("9. Create new branch");
        System.out.println("10. Delete branch");
        System.out.println("11. Checkout");
        System.out.println("12. Show commit history of current branch");
        System.out.println("13. Exit");
    }
}

import java.util.Scanner;


public class Playstation {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        while (t-- > 0) {
            int s1 = sc.nextInt();
            int s2 = sc.nextInt();
            int s3 = sc.nextInt();
            int p = sc.nextInt();

            if ((s1 + s2 >= p) || (s1 + s3 >= p) || (s2 + s3 >= p)) {
                System.out.println("Yes");
            } else {
                System.out.println("No");
            }
        }
    }
}


public class BinarySearchRecursion {

    public static void main(String[] args) {
        //for Initialization

    }


    // returns -1 if no element in array
    public static int binarySearch(int[] arr, int key, int low, int max) {

        int mid = low + (max - low) / 2;

        if ((mid == max || mid == low) && key != arr[mid]) {
            return -1;
        }

        if ( key > arr[mid]) {
            return binarySearch(arr,key,low = mid + 1, max) ;
        } else if (key < arr[mid]) {
            return binarySearch(arr,key,low,max = mid - 1);
        } else {
            return mid;
        }


    }
}

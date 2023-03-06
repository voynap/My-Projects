public class StringsLinkedList {
    private Node first = new Node();
    private Node last = new Node();

    public StringsLinkedList() {
        first.next = last ;
        last.prev = first ;
    }


    public void printAll() {
        Node currentElement = first.next;
        while ((currentElement) != null) {
            System.out.println(currentElement.value);
            currentElement = currentElement.next;
        }
    }

    public void add(String value) {

        Node node = new Node() ;
        node.value = value ;
        Node proxy = last.prev ;
        proxy.next = node;
        node.prev = proxy ;
        last.prev = node ;
    }


    public String get(int index) {
        int i = 0;
        Node node = first.next;
        if (index == 0)
            return first.next.value;
        do {
            i++;
            node = node.next;
            if (node == null)
                return null;
        } while (i != index);

        return node.value;
    }

        public static class Node {
        private Node prev;
        private String value;
        private Node next;
    }
}
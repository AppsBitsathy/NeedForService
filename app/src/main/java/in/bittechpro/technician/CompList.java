package in.bittechpro.technician;

class CompList {
    String[] list = {
            "W1",
            "W2",
            "W3",
            "W4",
            "W5",
            "B1",
            "B2",
            "B3",
            "B4",
            "B5",
            "U1",
            "U2",
            "U3",
            "U4",
            "U5"
    };
    CompList() {
    }
    String getComp(int i){
        if(i<=15)
            return list[i-1];
        else
            return "Complaint";
    }
}

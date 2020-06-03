package uk.ac.ucl.cs.sec.chainspace.bft;

/**
 * Created by sheharbano on 11/07/2017.
 */

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.json.JSONObject;
import uk.ac.ucl.cs.sec.chainspace.*;

public class Transaction implements Serializable {
    public String id;
    public List<String> inputs;
    public List<String> outputs;

    // Transaction states
    public static final String VALID = "valid";
    public static final String INVALID_NOOBJECT = "Invalid: Input object(s) doesn't exist.";
    public static final String INVALID_NOMANAGEDOBJECT = "Invalid: None of the input object(s) is managed by this shard.";
    public static final String REJECTED_LOCKEDOBJECT = "Rejected: Input object(s) is locked. ";
    public static final String INVALID_INACTIVEOBJECT = "Invalid: Input object(s) is inactive.";
    public static final String INVALID_BADTRANSACTION = "Invalid: Malformed transaction.";

    public Transaction() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }



    public void addID(String id) {
        this.id = id;
    }

    public void addInput(String in) {
        inputs.add(in);
    }

    public void addOutput(String in) {
        outputs.add(in);
    }

    public void print() {
        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inputs:\n");
        for (String s : inputs) {
            sb.append(s).append("\n");
        }
        sb.append("Outputs:\n");
        for (String s : outputs) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    public static byte[] toByteArray(Transaction t) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bs);
            os.writeObject(t);
            os.close();
            byte[] data = bs.toByteArray();
            return data;
        }
        catch (IOException ioe) {
            System.out.println("Exception: " + ioe.getMessage());
            return null;
        }
    }

    public static Transaction fromByteArray(byte[] data) {
        ByteArrayInputStream bs = new ByteArrayInputStream(data);
        try {
            ObjectInputStream os = new ObjectInputStream(bs);
            return (Transaction) os.readObject();
        }
        catch (Exception  e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }


    /*
        BLOCK ADDED
     */

    private CSTransaction csTransaction;
    private Store store;

    public Transaction(String request) throws AbortTransactionException, NoSuchAlgorithmException {
        this();

        // parse request
        JSONObject requestJson = new JSONObject(request);

        // extract transaction
        try { this.csTransaction = CSTransaction.fromJson(requestJson.getJSONObject("transaction")); }
        catch (Exception e) {
            throw new AbortTransactionException("Malformed transaction. [[" + request +"]]" , e);
        }

        // extract id-value store
        try { this.store = Store.fromJson(requestJson.getJSONObject("store")); }
        catch (Exception e) { throw new AbortTransactionException("Malformed id-value store.", e); }

        // extract id
        this.addID(this.csTransaction.getID());

        // init
        init(this.csTransaction, this.store);

    }

    // DEBUG CONSTRUCTOR
    public Transaction(CSTransaction csTransaction, Store store) throws NoSuchAlgorithmException {
        this();

        this.addID(csTransaction.getID());

        this.csTransaction = csTransaction;
        this.store = store;

        init(csTransaction, store);
    }

    private void init(CSTransaction csTransaction, Store store) throws NoSuchAlgorithmException {
        // add inputs and outputs to lists
        for (int i = 0; i < csTransaction.getInputIDs().length; i++) {
            this.addInput(csTransaction.getInputIDs()[i]);
        }
        for (int i = 0; i < csTransaction.getOutputs().length; i++) {
            String objectID = Utils.generateObjectID(csTransaction.getID(), csTransaction.getOutputs()[i], i);
            this.addOutput(objectID);
        }

        // process depdencies:
        // add inputs and outputs of all dependencies ot the input/output lists for BFT
        System.out.println("\n\n\n HERE\n" + csTransaction.getDependencies().length);
        for (int i = 0; i < csTransaction.getDependencies().length; i++) {
            // recursive calls
            init(csTransaction.getDependencies()[i], store);
        }
    }





    public CSTransaction getCsTransaction() {
        return csTransaction;
    }
    Store getStore() {
        return store;
    }

    /*
        END BLOCK
     */
}



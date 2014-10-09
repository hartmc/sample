package com.chess.board;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

public abstract class SerializationTest<T> extends TestCase {
    
    public File tmpFile = new File("tmpSerialization.txt");
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tmpFile.createNewFile();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        tmpFile.delete();
    }
    
    public T assertSerialize(T object) throws IOException, FileNotFoundException, ClassNotFoundException {
        writeObject(object);
        T result = readObject();
        assertEquals(object, result);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public T readObject() throws IOException, FileNotFoundException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tmpFile)));
        Object result = input.readObject();
        input.close();
        
        return (T) result;
    }
    
    public void writeObject(Object object) throws IOException, FileNotFoundException {
        ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));
        stream.writeObject(object);
        stream.close();
    }
}

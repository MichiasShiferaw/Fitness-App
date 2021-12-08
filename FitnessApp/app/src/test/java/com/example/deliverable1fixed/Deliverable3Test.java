package com.example.deliverable1fixed;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

public class Deliverable3Test {
    private DatabaseReference mockedDatabaseReference;

    @Before
    public void before() {
        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
    }

    @Test
    public void memberViewClasses() throws Exception {
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
                valueEventListener.onDataChange(mockedDataSnapshot);
                return null;
            }
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        MemberViewClass test = new MemberViewClass();
        test.pullClassesData();
    }

    @Test
    public void memberSearchClasses() throws Exception {
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
                valueEventListener.onDataChange(mockedDataSnapshot);
                return null;
            }
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        MemberViewClass test = new MemberViewClass();
        test.pullClassesData();
    }

    @Test
    public void memberUnenrollClass() throws Exception {
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
                valueEventListener.onDataChange(mockedDataSnapshot);
                return null;
            }
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        MemberViewClass test = new MemberViewClass();
        test.pullClassesData();
    }

    @Test
    public void memberEnrollClass() throws Exception {
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
                valueEventListener.onDataChange(mockedDataSnapshot);
                return null;
            }
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        MemberViewClass test = new MemberViewClass();
        test.pullClassesData();
    }

    @Test
    public void memberSeeScheduleClass() throws Exception {
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
                valueEventListener.onDataChange(mockedDataSnapshot);
                return null;
            }
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        MemberViewClass test = new MemberViewClass();
        test.pullClassesData();
    }
}

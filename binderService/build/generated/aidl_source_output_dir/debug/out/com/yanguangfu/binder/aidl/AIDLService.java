/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.yanguangfu.binder.aidl;
public interface AIDLService extends android.os.IInterface
{
  /** Default implementation for AIDLService. */
  public static class Default implements com.yanguangfu.binder.aidl.AIDLService
  {
    @Override public void registerTestCall(com.yanguangfu.binder.aidl.AIDLActivity cb) throws android.os.RemoteException
    {
    }
    @Override public void invokCallBack() throws android.os.RemoteException
    {
    }
    @Override public java.lang.String getName() throws android.os.RemoteException
    {
      return null;
    }
    @Override public int getAge() throws android.os.RemoteException
    {
      return 0;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.yanguangfu.binder.aidl.AIDLService
  {
    private static final java.lang.String DESCRIPTOR = "com.yanguangfu.binder.aidl.AIDLService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.yanguangfu.binder.aidl.AIDLService interface,
     * generating a proxy if needed.
     */
    public static com.yanguangfu.binder.aidl.AIDLService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.yanguangfu.binder.aidl.AIDLService))) {
        return ((com.yanguangfu.binder.aidl.AIDLService)iin);
      }
      return new com.yanguangfu.binder.aidl.AIDLService.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_registerTestCall:
        {
          data.enforceInterface(descriptor);
          com.yanguangfu.binder.aidl.AIDLActivity _arg0;
          _arg0 = com.yanguangfu.binder.aidl.AIDLActivity.Stub.asInterface(data.readStrongBinder());
          this.registerTestCall(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_invokCallBack:
        {
          data.enforceInterface(descriptor);
          this.invokCallBack();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.getName();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_getAge:
        {
          data.enforceInterface(descriptor);
          int _result = this.getAge();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.yanguangfu.binder.aidl.AIDLService
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void registerTestCall(com.yanguangfu.binder.aidl.AIDLActivity cb) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerTestCall, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerTestCall(cb);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void invokCallBack() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_invokCallBack, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().invokCallBack();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String getName() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getName, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getName();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getAge() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAge, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAge();
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static com.yanguangfu.binder.aidl.AIDLService sDefaultImpl;
    }
    static final int TRANSACTION_registerTestCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_invokCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getAge = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    public static boolean setDefaultImpl(com.yanguangfu.binder.aidl.AIDLService impl) {
      if (Stub.Proxy.sDefaultImpl == null && impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.yanguangfu.binder.aidl.AIDLService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void registerTestCall(com.yanguangfu.binder.aidl.AIDLActivity cb) throws android.os.RemoteException;
  public void invokCallBack() throws android.os.RemoteException;
  public java.lang.String getName() throws android.os.RemoteException;
  public int getAge() throws android.os.RemoteException;
}

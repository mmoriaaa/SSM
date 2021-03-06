/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs;

import org.apache.hadoop.fs.UnresolvedLinkException;
import org.smartdata.hdfs.CompatibilityHelperLoader;
import org.smartdata.model.FileState;

import java.io.IOException;

/**
 * Factory to create SmartInputStream with corresponding Hadoop version.
 */
public class SmartInputStreamFactory {
  /**
   *  Get HDFS input stream from dfsClient, file path and its file state.
   *
   * @param dfsClient HDFS client
   * @param src file path
   * @param fileState file state
   * @param verifyChecksum  check if need to checksum
   * @return HDFS input stream
   * @throws IOException if IOException occurs
   */
  public static DFSInputStream create(DFSClient dfsClient, String src,
                               boolean verifyChecksum, FileState fileState) throws IOException {
    dfsClient.checkOpen();
    return createSmartInputStream(dfsClient, src, verifyChecksum, fileState);
  }
  
  protected static DFSInputStream createSmartInputStream(DFSClient dfsClient, String src,
                                                  boolean verifyChecksum, FileState fileState) throws IOException {
    DFSInputStream inputStream;
    switch (fileState.getFileType()) {
      case NORMAL:
        inputStream = new SmartInputStream(dfsClient, src, verifyChecksum, fileState);
        break;
      case COMPACT:
        inputStream = new CompactInputStream(dfsClient, verifyChecksum, fileState);
        break;
      case COMPRESSION:
        inputStream = new CompressionInputStream(dfsClient, src, verifyChecksum, fileState);
        break;
      case S3:
        inputStream = new S3InputStream(dfsClient, src, verifyChecksum, fileState);
        break;
      default:
        throw new IOException("Unsupported file type");
    }
    return inputStream;
  }
}

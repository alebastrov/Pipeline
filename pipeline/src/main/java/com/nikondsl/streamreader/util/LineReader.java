package com.nikondsl.streamreader.util;

import com.nikondsl.streamreader.impl.LastStatus;
import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.ReadStatistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LineReader {
    private File file;
    private List<Pipeline> pipelines = new ArrayList<>();
    private final ReadStatistics readStatistics = new ReadStatistics(0);
	
	public LineReader(String path) throws IOException {
        this.file = Paths.get(path).toFile();
        if (!this.file.exists()) throw new FileNotFoundException("File " + path + " is not found");
        readStatistics.setLevel(ReadStatistics.ToStringLevel.NUMBERS);
    }

    public <T, R> LineReader addToPipeline(Pipeline<T, R> pipeline) {
        if (pipelines.isEmpty()) {
            if (!String.class.isAssignableFrom(pipeline.getArgumentClass())) {
                throw new IllegalArgumentException("First pipe should take only class 'String' as an argument, but was: " + pipeline.getArgumentClass().getCanonicalName());
            }
        } else {
            //take last element and check its result class
            Pipeline last = pipelines.get(pipelines.size()-1);
            if (!pipeline.getArgumentClass().isAssignableFrom(last.getResultClass())) {
                throw new IllegalArgumentException("This (#"+(pipelines.size()+1)+") pipe should take only class '"+last.getResultClass().getCanonicalName()+"' as an argument, but was: " + pipeline.getArgumentClass().getCanonicalName());
            }
        }
        pipelines.add(pipeline);
        return this;
    }
    
    public LineReader setLogLevel(ReadStatistics.ToStringLevel level) {
        readStatistics.setLevel(level);
        return this;
    }

    public Stream readLineByLine(Predicate<String> filter) throws IOException {
        if (pipelines == null || pipelines.isEmpty()) {
            throw new IllegalStateException("Please add at least one pipe before processing " + file.getAbsolutePath());
        }
        Queue<CompletableFuture<Object>> queue = new ConcurrentLinkedQueue<>();
        readStatistics.setFileSize(file.length());
        
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			reader
					.lines()
					.forEach(getConsumer(filter, queue));
		}
		
        return queue.stream();
    }
	
	private Consumer<String> getConsumer(Predicate<String> filter, Queue<CompletableFuture<Object>> queue) {
		return new Consumer<String>() {
			@Override
			public void accept(String line) {
				readStatistics.addToPosition(line.length() + 2);
				readStatistics.setLine(line);
				if (!filter.test(line)) return;
				CompletableFuture<Object> future = new CompletableFuture<>();
				queue.add(future);
				Object stepResult = line;
				LastStatus lastSuccessStep = LastStatus.BEFORE_BEGIN;
				Pipeline currentPipeline = null;
				int pipeNumber = 0;
				for (Pipeline pipeline : pipelines) {
					pipeNumber++;
					try {
						currentPipeline = pipeline;
						if (future.isCancelled()) {
							lastSuccessStep = LastStatus.CANCELLED;
							break;
						}
						if (stepResult == null) {
							if (lastSuccessStep != LastStatus.NOT_ALLOWED) {
								lastSuccessStep = LastStatus.PIPELINE_RETURNED_NULL;
							}
							break;
						}
						try {
							if (pipeline.isAllowed(stepResult)) {
								stepResult = pipeline.process(stepResult);
								lastSuccessStep = LastStatus.PROCESSED;
							} else {
								stepResult = null;
								lastSuccessStep = LastStatus.NOT_ALLOWED;
								break;
							}
						} catch (Exception ex) {
							lastSuccessStep = LastStatus.PROCESSING_ERROR;
						}
					} finally {
						readStatistics.setMessage("Pipe #" + pipeNumber + " " + lastSuccessStep);
					}
				}
				if (stepResult != null) {
					future.complete(stepResult);
				} else {
					future.completeExceptionally(new IOException("Pipeline [" + currentPipeline.getName() + "] cannot process line:" + line + " (" + lastSuccessStep + ")"));
				}
				String stat = readStatistics.toString();
				if (stat != null) System.err.println("" + stat);
			}
		};
	}
}

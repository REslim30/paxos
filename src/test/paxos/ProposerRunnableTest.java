package test.paxos;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import main.paxos.*;
import static main.paxos.MessageCodes.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;


/**
 * ProposerRunnabletest
 * Unit tests for proposer test
 */
public class ProposerRunnableTest {
    private BlockingQueue<String> messages;
    private DelayedMessageExecutor sender;
    private final int id = 5;
    private AtomicBoolean failure;
    private ExecutorService executor;

    @Before
    public void setFields() {
        messages = new LinkedBlockingQueue<String>();
        sender = mock(DelayedMessageExecutor.class);
        failure = new AtomicBoolean();
        executor = Executors.newFixedThreadPool(1000);
    }

    private ProposerRunnable initalizeProposerRunnable(int N, int timeToPropose) {
        return new ProposerRunnable(N, id, timeToPropose, messages, sender, failure);
    }

    @Test
    public void broadCastPrepareRequest() throws InterruptedException {
        executor.execute(initalizeProposerRunnable(20, 0));

        Thread.sleep(250);

        for (int i = 19; i >= 0; i--) {
            if (i != 5)
                verify(sender).send(String.format("%c%d %d",PREPARE, 5, 5),i);
        }
    }

    @Test
    public void broadCastPrepareRequestAfterDelay() throws InterruptedException {
        executor.execute(initalizeProposerRunnable(20, 500));

        for (int i = 0; i < 20; i++) {
            if (i != 5)
                verify(sender, never()).send(String.format("%c%d %d",PREPARE, 5, 5),i);
        }

        Thread.sleep(550);


        for (int i = 0; i < 20; i++) {
            if (i != 5)
                verify(sender).send(String.format("%c%d %d",PREPARE, 5, 5),i);
        }
    }

}
